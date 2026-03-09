package com.example.itemly.ui.viewModel

import android.content.Context
import com.example.itemly.data.api.ApiClient
import com.example.itemly.data.model.item.ItemsRequest
import com.example.itemly.data.model.item.SearchRequest

class HomeViewModel : BaseViewModel(
    { username, lastId ->
        val query = SearchState.query

        if (query.isNullOrBlank()) {
            ApiClient.apiService.getMain(ItemsRequest(username, lastId))
        } else {
            ApiClient.apiService.searchItems(SearchRequest(query, lastId))
        }
    }
) {
    object SearchState {
        var query: String? = null
    }

    fun startSearch(
        username: String,
        query: String,
        context: Context
    ) {
        SearchState.query = query
        loadFirstPage(username, context)
    }

    fun clearSearch(
        username: String,
        context: Context
    ) {
        SearchState.query = null
        loadFirstPage(username, context)
    }
}
