package com.example.itemly.ui.account

import android.os.Build
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

class AccountAuthor : Fragment() {
    private var _binding: FragmentAccountBinding? = null
    private val binding get() = _binding!!
    private val viewModel = AccountAuthorViewModel()
    private lateinit var usernameAuthor: String

    companion object {
        private const val ARG_ITEM = "arg_item"

        fun newInstance(data: String): AccountAuthor {
            return AccountAuthor().apply {
                arguments = Bundle().apply {
                    putString(ARG_ITEM, data)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        usernameAuthor = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requireArguments().getString(ARG_ITEM, "")!!
        } else {
            @Suppress("DEPRECATION")
            requireArguments().getString(ARG_ITEM) as String
        }
    }

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
        viewLifecycleOwner.lifecycleScope.launch {
            val layout = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            layout.gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_NONE

            val adapterItem = AdapterImageView(
                mutableListOf(),
                { item ->
                    (activity as? MainActivity)?.openDetailFragment(
                        DetailImageFragment.newInstance(
                            item
                        )
                    )
                },
                {}
            )

            val headerAdapter = HeaderAdapter(
                username = usernameAuthor,
                email = null,
                iconAccountUrl = getIconAccount(),
                userAuthor = true,
                onClickPreviewPhoto = {}
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
    }

    private suspend fun getIconAccount(): String? {
        return try {
            val response = ApiClient.apiService.getImageUser(usernameAuthor)
            response.pathPreview
        } catch (_: HttpException) {
            httpToast(requireContext())
            null
        } catch (_: IOException) {
            ioToast(requireContext())
            null
        }
    }
}