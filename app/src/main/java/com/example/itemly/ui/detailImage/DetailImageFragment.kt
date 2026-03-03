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
import com.example.itemly.ui.viewModel.FavoriteViewModel
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

        Glide.with(binding.mainImageFragmentDetailImage.context)
            .load(ApiConstants.BASE_URL + data.imageUrl)
            .into(binding.mainImageFragmentDetailImage)

        binding.buttonBackFragmentDetailImage.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        val adapter = AdapterTagsDetailImageFragment {} // TODO
        binding.includeBlockTagsDetailImageFragment.tagsRecyclerDetailImage.apply {
            this.adapter = adapter
            this.layoutManager = FlexboxLayoutManager(requireContext()).apply {
                flexDirection = FlexDirection.ROW
                flexWrap = FlexWrap.WRAP
            }
        }
        binding.recyclerFragmentDetailImage.apply {
            this.adapter = AdapterImageView(mutableListOf()) {}
            this.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        }

        getTags(adapter)
    }

    private fun getTags(adapter: AdapterTagsDetailImageFragment) {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = ApiClient.apiService.getTags(data.id)
                adapter.submitData(response)
            } catch (_: HttpException) {
                httpToast(context)
            } catch (_: IOException) {
                ioToast(context)
            }
        }
    }

    private fun saveItem() {
        val pref = requireContext().getSharedPreferences(PrefKeys.PREF_USER, Context.MODE_PRIVATE)
        val username = pref.getString(PrefKeys.USERNAME, "")!!

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                ApiClient.apiService.saveItem(ItemRequest(data.id, username))
                myImage.value = true
            } catch (_: HttpException) {
                httpToast(context)
            } catch (_: IOException) {
                ioToast(context)
            }
        }
    }

    private fun deleteItem() {
        val pref = requireContext().getSharedPreferences(PrefKeys.PREF_USER, Context.MODE_PRIVATE)
        val username = pref.getString(PrefKeys.USERNAME, "")!!

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                ApiClient.apiService.deleteFavoriteItem(ItemRequest(data.id, username))
                favoriteViewModel.removeItem(data.id)
                myImage.value = false
            } catch (_: HttpException) {
                httpToast(context)
            } catch (_: IOException) {
                ioToast(context)
            }
        }
    }
}
