package com.example.itemly.ui.home

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.itemly.data.objects.PrefKeys
import com.example.itemly.databinding.FragmentHomeBinding
import com.example.itemly.ui.components.imageVIew.AdapterImageView
import com.example.itemly.ui.detailImage.DetailImageFragment
import com.example.itemly.ui.main.MainActivity
import com.example.itemly.ui.viewModel.HomeViewModel

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeViewModel by activityViewModels()

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

        setupAdapter()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupAdapter() {
        val layout = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        layout.gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_NONE

        val adapter = AdapterImageView(mutableListOf()) { item ->
            if (!binding.editSearch.hasFocus())
                (activity as? MainActivity)?.openDetailFragment(DetailImageFragment(item))
        }

        binding.recyclerFragmentHome.apply {
            layoutManager = layout

            this.adapter = adapter

            setOnTouchListener { _, _ ->
                if (binding.editSearch.hasFocus()) {
                    binding.editSearch.clearFocus()

                    val sysService =
                        requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    sysService.hideSoftInputFromWindow(binding.editSearch.windowToken, 0)
                }
                false
            }
        }

        subscribeDataForAdapter(adapter, layout)
    }

    private fun subscribeDataForAdapter(
        adapter: AdapterImageView,
        layout: StaggeredGridLayoutManager
    ) {
        val pref = requireContext().getSharedPreferences(PrefKeys.PREF_USER, Context.MODE_PRIVATE)
        val username = pref.getString(PrefKeys.USERNAME, "")!!

        binding.recyclerFragmentHome.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val totalItemCount = layout.itemCount
                val lastVisibleItems = layout.findLastVisibleItemPositions(null)
                val lastVisibleItem = lastVisibleItems.maxOrNull() ?: 0

                if (lastVisibleItem + 5 >= totalItemCount) {
                    viewModel.loadNextPage(username, requireContext())
                }
            }
        })

        viewModel.items.observe(viewLifecycleOwner) { items ->
            adapter.submitList(items.toMutableList())
        }
        viewModel.loadFirstPage(username, requireContext())
    }
}
