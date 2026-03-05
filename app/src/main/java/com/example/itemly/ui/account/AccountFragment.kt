package com.example.itemly.ui.account

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

        setupUserData()
        setupAdapter()
    }

    private fun setupUserData() {
        val pref = requireActivity().getSharedPreferences(PrefKeys.PREF_USER, Context.MODE_PRIVATE)
        val username = pref.getString(PrefKeys.USERNAME, "")!!
        val email = pref.getString(PrefKeys.E_MAIL, "")!!

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = ApiClient.apiService.getImageUser(username)
                Glide.with(binding.imageAccount.context)
                    .load(ApiConstants.BASE_URL + response)
                    .placeholder(R.drawable.ic_account)
                    .error(R.drawable.ic_account)
                    .fallback(R.drawable.ic_account)
                    .into(binding.imageAccount)
            } catch (_: HttpException) {
                httpToast(requireContext())
            } catch (_: IOException) {
                ioToast(requireContext())
            }
        }

        binding.usernameAccount.text = username
        binding.emailAccount.text = email
        binding.imageAccount.setColorFilter(
            ContextCompat.getColor(requireContext(), R.color.black)
        )
    }

    private fun setupAdapter() {
        val layout = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        layout.gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_NONE

        val adapter = AdapterImageView(mutableListOf()) { item ->
            (activity as? MainActivity)?.openDetailFragment(
                DetailImageFragment(
                    item,
                    MutableLiveData(false)
                )
            )
        }

        binding.recyclerItems.apply {
            this.adapter = adapter
            this.layoutManager = layout
            this.addItemDecoration(StaggeredGridSpacingItemDecoration(2, 10, true))
        }

        subscribeDataForAdapter(
            requireContext(),
            binding.recyclerItems,
            adapter,
            layout,
            viewLifecycleOwner,
            viewModel
        )
    }
}
