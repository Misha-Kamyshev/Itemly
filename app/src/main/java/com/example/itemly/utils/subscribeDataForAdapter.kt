package com.example.itemly.utils

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.lifecycle.LifecycleOwner
import com.example.itemly.ui.components.imageVIew.AdapterImageView
import com.example.itemly.ui.viewModel.BaseViewModel

fun subscribeDataForAdapter(
    context: Context,
    recycler: RecyclerView,
    adapter: AdapterImageView,
    layout: StaggeredGridLayoutManager,
    viewLifecycleOwner: LifecycleOwner,
    viewModel: BaseViewModel
) {
    recycler.addOnScrollListener(object :
        RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            val totalItemCount = layout.itemCount
            val lastVisibleItems = layout.findLastVisibleItemPositions(null)
            val lastVisibleItem = lastVisibleItems.maxOrNull() ?: 0

            if (lastVisibleItem + 5 >= totalItemCount) {
                viewModel.loadNextPage(context)
            }
        }
    })

    viewModel.items.observe(viewLifecycleOwner) { items ->
        if (items.isEmpty()) {
            adapter.submitList(items.toMutableList(), clear = true)
        } else {
            adapter.submitList(items.toMutableList())
        }
    }
    viewModel.loadFirstPage(context)
}
