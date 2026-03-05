package com.example.itemly.ui.detailImage

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.example.itemly.R
import com.example.itemly.data.api.ApiClient
import com.example.itemly.data.api.ApiConstants
import com.example.itemly.data.model.item.ItemDataSchema
import com.example.itemly.data.model.item.ItemRequest
import com.example.itemly.data.objects.PrefKeys
import com.example.itemly.databinding.FragmentDetailImageBinding
import com.example.itemly.ui.components.imageVIew.AdapterImageView
import com.example.itemly.ui.components.httpToast
import com.example.itemly.ui.components.ioToast
import com.example.itemly.ui.main.MainActivity
import com.example.itemly.ui.viewModel.FavoriteViewModel
import com.example.itemly.utils.StaggeredGridSpacingItemDecoration
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class DetailImageFragment(
    private val data: ItemDataSchema,
    private val myImage: MutableLiveData<Boolean>
) : Fragment() {
    private var _binding: FragmentDetailImageBinding? = null
    private val binding get() = _binding!!
    private val favoriteViewModel: FavoriteViewModel by activityViewModels()
    private lateinit var username: String
    private val countLike = MutableLiveData(0)
    private val countComment = MutableLiveData(0)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailImageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val pref = requireContext().getSharedPreferences(PrefKeys.PREF_USER, Context.MODE_PRIVATE)
        username = pref.getString(PrefKeys.USERNAME, "")!!

        observeUI()

        Glide.with(binding.mainImageFragmentDetailImage.context)
            .load(ApiConstants.BASE_URL + data.imageUrl)
            .into(binding.mainImageFragmentDetailImage)

        binding.buttonBackFragmentDetailImage.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.includeBlockTagsDetailImageFragment.apply {
            imageLike.setOnClickListener { onClickLike() }
        }

        setupAdapters()
    }

    private fun observeUI() {
        myImage.observe(viewLifecycleOwner) {
            if (!it) {
                binding.includeBlockTagsDetailImageFragment.containerSaveBlockTagsDetailImage.apply {
                    text = ContextCompat.getString(
                        requireContext(),
                        R.string.buttonSaveTagsDetailImage
                    )
                    setOnClickListener { saveItem() }
                }
            } else {
                binding.includeBlockTagsDetailImageFragment.containerSaveBlockTagsDetailImage.apply {
                    text = ContextCompat.getString(
                        requireContext(),
                        R.string.buttonDeleteTagsDetailImage
                    )
                    setOnClickListener { deleteItem() }
                }
            }
        }

        countLike.observe(viewLifecycleOwner) {
            binding.includeBlockTagsDetailImageFragment.countLike.text =
                countLike.value!!.toString()
        }

        countComment.observe(viewLifecycleOwner) {
            binding.includeBlockTagsDetailImageFragment.countComment.text =
                countComment.value!!.toString()
        }
    }

    private fun setupAdapters() {
        val adapter = AdapterTagsDetailImageFragment {} // TODO
        binding.includeBlockTagsDetailImageFragment.tagsRecyclerDetailImage.apply {
            this.adapter = adapter
            this.layoutManager = FlexboxLayoutManager(requireContext()).apply {
                flexDirection = FlexDirection.ROW
                flexWrap = FlexWrap.WRAP
            }
        }
        getInformation(adapter)

        val layout = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        layout.gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_NONE

        val adapterImage = AdapterImageView(mutableListOf()) { item ->
            (activity as? MainActivity)?.openDetailFragment(
                DetailImageFragment(
                    item,
                    MutableLiveData(false)
                )
            )
        }

        binding.recyclerFragmentDetailImage.apply {
            this.adapter = adapterImage
            this.layoutManager = layout
            this.addItemDecoration(StaggeredGridSpacingItemDecoration(2, 10, true))
        }
    }

    private fun getInformation(adapter: AdapterTagsDetailImageFragment) {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = ApiClient.apiService.getInformation(data.id)
                adapter.submitData(response.tags)
                binding.includeBlockTagsDetailImageFragment.apply {
                    countLike.text = response.countLike.toString()
                    countComment.text = response.countComment.toString()
                    usernameAuthor.text = response.author
                    nameItem.text = response.name

                    Glide.with(requireContext())
                        .load(ApiConstants.BASE_URL + response.iconAuthor)
                        .into(imageUserPushDetailImage)
                }
            } catch (_: HttpException) {
                httpToast(context)
                requireActivity().onBackPressedDispatcher.onBackPressed()
            } catch (_: IOException) {
                ioToast(context)
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
        }
    }

    private fun onClickLike() {
        val imageLike = binding.includeBlockTagsDetailImageFragment.imageLike
        val isSelected = imageLike.isSelected

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                if (!isSelected) {
                    val response =
                        ApiClient.apiService.addLike(ItemRequest(data.id, username))
                    if (response.isSuccessful) {
                        countLike.value = countLike.value!! + 1
                        imageLike.isSelected = true
                    } else {
                        httpToast(context)
                    }
                } else {
                    val response =
                        ApiClient.apiService.deleteLike(ItemRequest(data.id, username))
                    if (response.isSuccessful) {
                        countLike.value = countLike.value!! - 1
                        imageLike.isSelected = false
                    } else {
                        httpToast(context)
                    }
                }
            } catch (_: IOException) {
                ioToast(context)
            }
        }
    }

    private fun onClickComment() {
        // TODO
    }

    private fun saveItem() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = ApiClient.apiService.saveItem(ItemRequest(data.id, username))
                if (response.isSuccessful) {
                    myImage.value = true
                } else {
                    httpToast(context)
                }
            } catch (_: IOException) {
                ioToast(context)
            }
        }
    }

    private fun deleteItem() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response =
                    ApiClient.apiService.deleteFavoriteItem(ItemRequest(data.id, username))
                if (response.isSuccessful) {
                    favoriteViewModel.removeItem(data.id)
                    myImage.value = false
                } else {
                    httpToast(context)
                }
            } catch (_: IOException) {
                ioToast(context)
            }
        }
    }
}
