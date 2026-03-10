package com.example.itemly.ui.account

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
import com.example.itemly.data.objects.PrefKeys
import com.example.itemly.databinding.FragmentAccountBinding
import com.example.itemly.ui.components.httpToast
import com.example.itemly.ui.components.imageVIew.AdapterImageView
import com.example.itemly.ui.components.ioToast
import com.example.itemly.ui.detailImage.DetailImageFragment
import com.example.itemly.ui.main.MainActivity
import com.example.itemly.ui.viewModel.AccountViewModel
import com.example.itemly.utils.StaggeredGridSpacingItemDecoration
import com.example.itemly.utils.subscribeDataForAdapter
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class AccountFragment : Fragment() {
    private var _binding: FragmentAccountBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AccountViewModel by activityViewModels()
    private lateinit var username: String
    private lateinit var email: String
    private lateinit var headerAdapter: HeaderAdapter
    private lateinit var adapterItem: AdapterImageView

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
        val pref = requireActivity().getSharedPreferences(PrefKeys.PREF_USER, Context.MODE_PRIVATE)
        username = pref.getString(PrefKeys.USERNAME, "")!!
        email = pref.getString(PrefKeys.E_MAIL, "")!!

        setupAdapter()
    }

    private suspend fun getIconAccount(): String? {
        return try {
            val response = ApiClient.apiService.getImageUser(username)
            response.pathPreview
        } catch (_: HttpException) {
            httpToast(requireContext())
            null
        } catch (_: IOException) {
            ioToast(requireContext())
            null
        }
    }

    private fun setupAdapter() {
        val layout = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        layout.gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_NONE

        adapterItem = AdapterImageView(mutableListOf()) { item ->
            (activity as? MainActivity)?.openDetailFragment(DetailImageFragment.newInstance(item))
        }

        headerAdapter = HeaderAdapter(
            username = username,
            email = email,
            iconAccountUrl = getIconAccount(),
            userAuthor = false
            iconAccountUrl = null,
            userAuthor = false,
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

        loadAvatar()
    }

    private fun loadAvatar() {
        viewLifecycleOwner.lifecycleScope.launch {
            val icon = getIconAccount()

            headerAdapter.updateIcon(icon)
            headerAdapter.notifyItemChanged(0)
        }
    }
    }
}
