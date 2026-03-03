package com.example.itemly.ui.myImage

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.itemly.data.objects.PrefKeys
import com.example.itemly.databinding.FragmentMyImageBinding
import com.example.itemly.ui.components.imageVIew.AdapterImageView
import com.example.itemly.ui.detailImage.DetailImageFragment
import com.example.itemly.ui.main.MainActivity
import com.example.itemly.ui.viewModel.MyImageViewModel
import com.example.itemly.utils.StaggeredGridSpacingItemDecoration

class MyImageFragment : Fragment() {
    private var _binding: FragmentMyImageBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MyImageViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyImageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecycler()
    }

    private fun setupRecycler() {
        val adapter = AdapterImageView(mutableListOf()) { item ->
            (activity as? MainActivity)?.openDetailFragment(
                DetailImageFragment(
                    item,
                    MutableLiveData(false)
                )
            )
        }

        val layout = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        layout.gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_NONE

        binding.recyclerFragmentMyImage.apply {
            this.adapter = adapter
            this.layoutManager = layout
            this.addItemDecoration(
                StaggeredGridSpacingItemDecoration(2, 10, true)
            )
        }

        subscribeDataForAdapter(adapter, layout)
    }

    private fun subscribeDataForAdapter(
        adapter: AdapterImageView,
        layout: StaggeredGridLayoutManager
    ) {
        val pref = requireContext().getSharedPreferences(PrefKeys.PREF_USER, Context.MODE_PRIVATE)
        val username = pref.getString(PrefKeys.USERNAME, "")!!

        binding.recyclerFragmentMyImage.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
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