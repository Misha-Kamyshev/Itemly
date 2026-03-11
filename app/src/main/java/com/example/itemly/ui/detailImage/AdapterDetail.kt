package com.example.itemly.ui.detailImage

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.itemly.R
import com.example.itemly.data.api.ApiClient
import com.example.itemly.data.api.ApiConstants
import com.example.itemly.data.model.item.ItemData
import com.example.itemly.data.model.item.ItemInformationResponse
import com.example.itemly.data.objects.PrefKeys
import com.example.itemly.databinding.BlockDetailHeaderBinding
import com.example.itemly.ui.components.httpToast
import com.example.itemly.ui.components.ioToast
import com.example.itemly.utils.requestWithTokenRetry
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import kotlinx.coroutines.launch
import java.io.IOException

class AdapterDetail(
    private val data: ItemData,
    private val info: ItemInformationResponse,
    private val lifecycleOwner: LifecycleOwner,
    private val onClickBack: () -> Unit,
    private val onClickOther: (String, View) -> Unit,
    private val onClickAuthor: (String) -> Unit,
    private val onClickTag: (String) -> Unit
) : RecyclerView.Adapter<AdapterDetail.ViewHolder>() {
    private val saveItem = MutableLiveData(false)
    private val countLike = MutableLiveData(0)
    private lateinit var accessToken: String

    class ViewHolder(
        val binding: BlockDetailHeaderBinding
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = BlockDetailHeaderBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        observeUI(binding)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val binding = holder.binding
        val context = binding.root.context

        val pref = context.getSharedPreferences(PrefKeys.PREF_USER, Context.MODE_PRIVATE)
        accessToken = pref.getString(PrefKeys.ACCESS_TOKEN, "")!!

        Glide.with(context)
            .load(ApiConstants.BASE_URL + data.imageUrl)
            .into(binding.mainImageFragmentDetailImage)

        binding.buttonBackFragmentDetailImage.setOnClickListener {
            onClickBack()
        }
        binding.buttonOtherFragmentDetailImage.setOnClickListener {
            onClickOther(info.author, it)
        }

        val adapterTags = AdapterTagsDetailImageFragment {
            onClickTag(it)
        }

        binding.includeBlockTagsDetailImageFragment.tagsRecyclerDetailImage.apply {
            adapter = adapterTags
            layoutManager = FlexboxLayoutManager(context).apply {
                flexDirection = FlexDirection.ROW
                flexWrap = FlexWrap.WRAP
            }
        }

        loadInformation(binding, adapterTags)
        binding.includeBlockTagsDetailImageFragment.apply {
            imageLike.setOnClickListener { onClickLike(imageLike, binding.root.context) }
            blockAuthor.setOnClickListener { onClickAuthor(info.author) }
            buttonGoToAuthor.setOnClickListener { onClickAuthor(info.author) }
        }
    }

    override fun getItemCount() = 1

    override fun onViewAttachedToWindow(holder: ViewHolder) {
        val params = holder.itemView.layoutParams as StaggeredGridLayoutManager.LayoutParams
        params.isFullSpan = true
    }

    private fun loadInformation(
        binding: BlockDetailHeaderBinding,
        adapter: AdapterTagsDetailImageFragment
    ) {
        adapter.submitData(info.tags)

        binding.includeBlockTagsDetailImageFragment.apply {
            usernameAuthor.text = info.author
            nameItem.text = info.name

            imageLike.isSelected = info.likeItem
        }

        saveItem.value = info.saveItem
        countLike.value = info.countLike

        if (info.iconAuthor.isNullOrEmpty()) {
            binding.includeBlockTagsDetailImageFragment.apply {
                iconUserPushDetailImage.visibility = View.VISIBLE
                imageUserPushDetailImage.visibility = View.GONE
            }
        } else {
            binding.includeBlockTagsDetailImageFragment.apply {
                iconUserPushDetailImage.visibility = View.GONE
                imageUserPushDetailImage.visibility = View.VISIBLE

                Glide.with(imageUserPushDetailImage.context)
                    .load(ApiConstants.BASE_URL + info.iconAuthor)
                    .error(R.drawable.ic_account)
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(imageUserPushDetailImage)
            }
        }
    }

    private fun observeUI(
        binding: BlockDetailHeaderBinding,
    ) {
        val context = binding.root.context

        saveItem.observe(lifecycleOwner) {
            if (!it) {
                binding.includeBlockTagsDetailImageFragment.containerSaveBlockTagsDetailImage.apply {
                    text = ContextCompat.getString(
                        context,
                        R.string.buttonSaveTagsDetailImage
                    )
                    setOnClickListener { saveItem(true, context) }
                }
            } else {
                binding.includeBlockTagsDetailImageFragment.containerSaveBlockTagsDetailImage.apply {
                    text = ContextCompat.getString(
                        context,
                        R.string.buttonDeleteTagsDetailImage
                    )
                    setOnClickListener { saveItem(false, context) }
                }
            }
        }

        countLike.observe(lifecycleOwner) {
            binding.includeBlockTagsDetailImageFragment.countLike.text =
                countLike.value!!.toString()
        }
    }

    private fun onClickLike(imageLike: ImageView, context: Context) {
        val isSelected = imageLike.isSelected

        lifecycleOwner.lifecycleScope.launch {
            try {
                val response = requestWithTokenRetry(context) { token ->
                    if (!isSelected) {
                        ApiClient.apiService.addLike(data.id, "Bearer $token")
                    } else {
                        ApiClient.apiService.deleteLike(data.id, "Bearer $token")
                    }
                }

                if (response.isSuccessful) {
                    if (!isSelected) {
                        countLike.value = countLike.value!! + 1
                        imageLike.isSelected = true
                    } else {
                        countLike.value = countLike.value!! - 1
                        imageLike.isSelected = false
                    }
                } else {
                    httpToast(imageLike.context)
                }
            } catch (_: IOException) {
                ioToast(imageLike.context)
            }
        }
    }

    private fun saveItem(save: Boolean, context: Context) {
        lifecycleOwner.lifecycleScope.launch {
            try {
                val response = requestWithTokenRetry(context) { token ->
                    if (save) {
                        ApiClient.apiService.saveItem(data.id, "Bearer $token")
                    } else {
                        ApiClient.apiService.deleteFavoriteItem(data.id, "Bearer $token")
                    }
                }

                if (response.isSuccessful) {
                    saveItem.value = save
                } else {
                    httpToast(context)
                }
            } catch (_: IOException) {
                ioToast(context)
            }
        }
    }
}
