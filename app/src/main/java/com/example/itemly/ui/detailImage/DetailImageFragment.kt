package com.example.itemly.ui.detailImage

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.itemly.data.api.ApiClient
import com.example.itemly.data.model.item.ItemDataSchema
import com.example.itemly.data.model.item.ItemInformation
import com.example.itemly.data.objects.PrefKeys
import com.example.itemly.databinding.FragmentDetailImageBinding
import com.example.itemly.ui.accountAuthor.AccountAuthor
import com.example.itemly.ui.components.imageVIew.AdapterImageView
import com.example.itemly.ui.components.httpToast
import com.example.itemly.ui.components.ioToast
import com.example.itemly.ui.main.MainActivity
import com.example.itemly.ui.viewModel.FavoriteViewModel
import com.example.itemly.ui.viewModel.DetailImageViewModel
import com.example.itemly.utils.StaggeredGridSpacingItemDecoration
import com.example.itemly.utils.subscribeDataForAdapter
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class DetailImageFragment(private val data: ItemDataSchema) : Fragment() {
    private var _binding: FragmentDetailImageBinding? = null
    private val binding get() = _binding!!
    private val favoriteViewModel: FavoriteViewModel by activityViewModels()
    private lateinit var username: String

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

        setupAdapters()
    }


    private fun setupAdapters() {
        viewLifecycleOwner.lifecycleScope.launch {
            val info = getInformation()

            val layout = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            layout.gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_NONE

            val headerAdapter = AdapterDetail(
                data,
                username,
                info,
                viewLifecycleOwner,
                favoriteViewModel,
                onClickBack = { requireActivity().onBackPressedDispatcher.onBackPressed() },
                onClickAuthor = {
                    (activity as? MainActivity)?.openDetailFragment(
                        AccountAuthor(info.author)
                    )
                }
            )

            val adapterImage = AdapterImageView(mutableListOf()) { data ->
                (activity as? MainActivity)?.openDetailFragment(DetailImageFragment(data))
            }

            val concatAdapter = ConcatAdapter(headerAdapter, adapterImage)

            binding.recyclerFragmentDetailImage.apply {
                this.adapter = concatAdapter
                this.layoutManager = layout
                this.addItemDecoration(StaggeredGridSpacingItemDecoration(2, 10, true))
            }

            val viewModel = DetailImageViewModel(info.tags)

            subscribeDataForAdapter(
                requireContext(),
                binding.recyclerFragmentDetailImage,
                adapterImage,
                layout,
                viewLifecycleOwner,
                viewModel
            )
        }
    }

    suspend fun getInformation(): ItemInformation {
        try {
            return ApiClient.apiService.getInformation(data.id, username)
        } catch (_: HttpException) {
            httpToast(context)
            requireActivity().onBackPressedDispatcher.onBackPressed()
            throw CancellationException()
        } catch (_: IOException) {
            ioToast(context)
            requireActivity().onBackPressedDispatcher.onBackPressed()
            throw CancellationException()
        }
    }
}
