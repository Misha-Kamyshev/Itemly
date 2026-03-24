package com.example.itemly.ui.searchTag

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.itemly.databinding.FragmentHomeBinding
import com.example.itemly.ui.components.imageVIew.AdapterImageView
import com.example.itemly.ui.detailImage.DetailImageFragment
import com.example.itemly.ui.main.MainActivity
import com.example.itemly.ui.viewModel.SearchTagViewModel
import com.example.itemly.utils.StaggeredGridSpacingItemDecoration
import com.example.itemly.utils.subscribeDataForAdapter

class SearchTagFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SearchTagViewModel by viewModels()
    private lateinit var tag: String

    companion object {
        private const val ARG_ITEM = "arg_item"

        fun newInstance(tag: String): SearchTagFragment {
            return SearchTagFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_ITEM, tag)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        tag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
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
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.editSearch.apply {
            isClickable = false
            isFocusableInTouchMode = false
            setText(this@SearchTagFragment.tag)
        }

        viewModel.startSearch(tag)

        setupAdapter()
    }

    private fun setupAdapter() {
        val layout = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        layout.gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_NONE

        val adapter = AdapterImageView(
            mutableListOf(),
            { data ->
                if (!binding.editSearch.hasFocus())
                    (activity as? MainActivity)?.openDetailFragment(
                        DetailImageFragment.newInstance(
                            data
                        )
                    )
            },
            {}
        )

        binding.recyclerFragmentHome.apply {
            this.adapter = adapter
            this.layoutManager = layout
            this.addItemDecoration(
                StaggeredGridSpacingItemDecoration(2, 10, true)
            )
        }
        subscribeDataForAdapter(
            requireContext(),
            binding.recyclerFragmentHome,
            adapter,
            layout,
            viewLifecycleOwner,
            viewModel
        )
    }
}
