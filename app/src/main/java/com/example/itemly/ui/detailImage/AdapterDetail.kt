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
import com.example.itemly.data.model.item.ItemDataSchema
import com.example.itemly.data.model.item.ItemInformation
import com.example.itemly.data.model.item.ItemRequest
import com.example.itemly.databinding.BlockDetailHeaderBinding
import com.example.itemly.ui.components.httpToast
import com.example.itemly.ui.components.ioToast
import com.example.itemly.ui.viewModel.FavoriteViewModel
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import kotlinx.coroutines.launch
import java.io.IOException

class AdapterDetail(
    private val data: ItemDataSchema,
    private val username: String,
    private val info: ItemInformation,
    private val lifecycleOwner: LifecycleOwner,
    private val favoriteViewModel: FavoriteViewModel,
    private val onClickBack: () -> Unit,
    private val onClickOther: (String, View) -> Unit,
    private val onClickAuthor: (String) -> Unit,
    private val onClickTag: (String) -> Unit
) : RecyclerView.Adapter<AdapterDetail.ViewHolder>() {
    private val saveItem = MutableLiveData(false)
    private val countLike = MutableLiveData(0)

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
            imageLike.setOnClickListener { onClickLike(imageLike) }
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

    private fun onClickLike(imageLike: ImageView) {
        val isSelected = imageLike.isSelected

        lifecycleOwner.lifecycleScope.launch {
            try {
                if (!isSelected) {
                    val response =
                        ApiClient.apiService.addLike(ItemRequest(data.id, username))
                    if (response.isSuccessful) {
                        countLike.value = countLike.value!! + 1
                        imageLike.isSelected = true
                    } else {
                        httpToast(imageLike.context)
                    }
                } else {
                    val response =
                        ApiClient.apiService.deleteLike(ItemRequest(data.id, username))
                    if (response.isSuccessful) {
                        countLike.value = countLike.value!! - 1
                        imageLike.isSelected = false
                    } else {
                        httpToast(imageLike.context)
                    }
                }
            } catch (_: IOException) {
                ioToast(imageLike.context)
            }
        }
    }

    private fun saveItem(save: Boolean, context: Context) {
        lifecycleOwner.lifecycleScope.launch {
            try {
                if (save) {
                    val response = ApiClient.apiService.saveItem(ItemRequest(data.id, username))
                    if (response.isSuccessful) {
                        saveItem.value = true
                    } else {
                        httpToast(context)
                    }
                } else {
                    val response =
                        ApiClient.apiService.deleteFavoriteItem(ItemRequest(data.id, username))
                    if (response.isSuccessful) {
                        favoriteViewModel.removeItem(data.id)
                        saveItem.value = false
                    } else {
                        httpToast(context)
                    }
                }
            } catch (_: IOException) {
                ioToast(context)
            }
        }
    }
}
