package com.example.itemly.ui.viewModel

import android.content.Context
import com.example.itemly.data.api.ApiClient
import com.example.itemly.data.model.item.SearchRequest

class HomeViewModel : BaseViewModel(
    { lastId, accessToken ->
        val query = SearchState.query

        if (query.isNullOrBlank()) {
            ApiClient.apiService.getMain(lastId, "Bearer $accessToken")
        } else {
            ApiClient.apiService.searchItems(SearchRequest(query, lastId), "Bearer $accessToken")
        }
    }
) {
    object SearchState {
        var query: String? = null
    }

    fun startSearch(
        query: String,
        context: Context
    ) {
        SearchState.query = query
        loadFirstPage(context)
    }

    fun clearSearch(
        context: Context
    ) {
        SearchState.query = null
        loadFirstPage(context)
    }
}
