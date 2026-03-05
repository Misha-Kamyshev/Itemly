package com.example.itemly.utils

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.itemly.data.objects.PrefKeys
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
    val pref = context.getSharedPreferences(PrefKeys.PREF_USER, Context.MODE_PRIVATE)
    val username = pref.getString(PrefKeys.USERNAME, "")!!

    recycler.addOnScrollListener(object :
        RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            val totalItemCount = layout.itemCount
            val lastVisibleItems = layout.findLastVisibleItemPositions(null)
            val lastVisibleItem = lastVisibleItems.maxOrNull() ?: 0

            if (lastVisibleItem + 5 >= totalItemCount) {
                viewModel.loadNextPage(username, context)
            }
        }
    })

    viewModel.items.observe(viewLifecycleOwner) { items ->
        adapter.submitList(items.toMutableList())
    }
    viewModel.loadFirstPage(username, context)
}