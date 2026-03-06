package com.example.itemly.ui.accountAuthor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.itemly.data.api.ApiClient
import com.example.itemly.databinding.FragmentAccountBinding
import com.example.itemly.ui.account.HeaderAdapter
import com.example.itemly.ui.components.httpToast
import com.example.itemly.ui.components.imageVIew.AdapterImageView
import com.example.itemly.ui.components.ioToast
import com.example.itemly.ui.detailImage.DetailImageFragment
import com.example.itemly.ui.main.MainActivity
import com.example.itemly.ui.viewModel.AccountAuthorViewModel
import com.example.itemly.utils.StaggeredGridSpacingItemDecoration
import com.example.itemly.utils.subscribeDataForAdapter
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class AccountAuthor(
    private val usernameAuthor: String
) : Fragment() {
    private var _binding: FragmentAccountBinding? = null
    private val binding get() = _binding!!
    private val viewModel = AccountAuthorViewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupAdapter()
    }

    private fun setupAdapter() {
        val layout = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        layout.gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_NONE

        val adapterItem = AdapterImageView(mutableListOf()) { item ->
            (activity as? MainActivity)?.openDetailFragment(DetailImageFragment(item))
        }

        val headerAdapter = HeaderAdapter(
            username = usernameAuthor,
            email = null,
            iconAccountUrl = getIconAccount(),
            userAuthor = true
        )

        val concatAdapter = ConcatAdapter(headerAdapter, adapterItem)

        binding.recyclerAccount.apply {
            this.adapter = concatAdapter
            this.layoutManager = layout
            this.addItemDecoration(StaggeredGridSpacingItemDecoration(2, 10, true))
        }

        subscribeDataForAdapter(
            requireContext(),
            binding.recyclerAccount,
            adapterItem,
            layout,
            viewLifecycleOwner,
            viewModel
        )
    }

    private fun getIconAccount(): String? {
        var iconAccountUrl: String? = null

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = ApiClient.apiService.getImageUser(usernameAuthor)
                iconAccountUrl = response.pathPreview
            } catch (_: HttpException) {
                httpToast(requireContext())
            } catch (_: IOException) {
                ioToast(requireContext())
            }
        }

        return iconAccountUrl
    }
}
