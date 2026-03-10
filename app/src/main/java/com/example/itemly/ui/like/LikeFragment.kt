package com.example.itemly.ui.like

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.itemly.data.objects.PrefKeys
import com.example.itemly.databinding.FragmentLikeBinding
import com.example.itemly.ui.components.imageVIew.AdapterImageView
import com.example.itemly.ui.detailImage.DetailImageFragment
import com.example.itemly.ui.main.MainActivity
import com.example.itemly.ui.viewModel.LikeViewModel
import com.example.itemly.utils.StaggeredGridSpacingItemDecoration
import com.example.itemly.utils.subscribeDataForAdapter

class LikeFragment : Fragment() {
    private var _binding: FragmentLikeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: LikeViewModel by activityViewModels()
    private lateinit var adapterItem: AdapterImageView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLikeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecycler()
        binding.swipeRefreshLike.setOnRefreshListener {
            refresh()
            binding.swipeRefreshLike.isRefreshing = false
        }
    }

    private fun setupRecycler() {
        val layout = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        layout.gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_NONE

        adapterItem = AdapterImageView(mutableListOf()) { item ->
            (activity as? MainActivity)?.openDetailFragment(DetailImageFragment.newInstance(item))
        }

        binding.recyclerFragmentMyImage.apply {
            this.adapter = adapterItem
            this.layoutManager = layout
            this.addItemDecoration(
                StaggeredGridSpacingItemDecoration(2, 10, true)
            )
        }

        subscribeDataForAdapter(
            requireContext(),
            binding.recyclerFragmentMyImage,
            adapterItem,
            layout,
            viewLifecycleOwner,
            viewModel
        )
    }

    private fun refresh() {
        val pref = requireContext().getSharedPreferences(PrefKeys.PREF_USER, Context.MODE_PRIVATE)
        val username = pref.getString(PrefKeys.USERNAME, "")!!

        viewModel.refresh(username, requireContext())
    }
}
