package com.example.itemly.ui.favorite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.itemly.databinding.FragmentFavoriteBinding
import com.example.itemly.ui.components.imageVIew.AdapterImageView
import com.example.itemly.ui.detailImage.DetailImageFragment
import com.example.itemly.ui.main.MainActivity
import com.example.itemly.ui.viewModel.FavoriteViewModel
import com.example.itemly.utils.StaggeredGridSpacingItemDecoration
import com.example.itemly.utils.subscribeDataForAdapter

class FavoriteFragment : Fragment() {
    private var _binding: FragmentFavoriteBinding? = null
    private val binding get() = _binding!!
    private val viewModel: FavoriteViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecycler()
    }

    private fun setupRecycler() {
        val adapter = AdapterImageView(mutableListOf()) { data ->
            (activity as? MainActivity)?.openDetailFragment(DetailImageFragment.newInstance(data))
        }

        val layout = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        layout.gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_NONE

        binding.recyclerFragmentFavorite.apply {
            this.adapter = adapter
            this.layoutManager = layout
            this.addItemDecoration(
                StaggeredGridSpacingItemDecoration(2, 10, true)
            )
        }

        subscribeDataForAdapter(
            requireContext(),
            binding.recyclerFragmentFavorite,
            adapter,
            layout,
            viewLifecycleOwner,
            viewModel
        )
    }
}
