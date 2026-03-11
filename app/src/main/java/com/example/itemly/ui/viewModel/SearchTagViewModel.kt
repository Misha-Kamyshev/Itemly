package com.example.itemly.ui.viewModel

import android.content.Context
import com.example.itemly.data.api.ApiClient
import com.example.itemly.data.model.item.SearchRequest

class SearchTagViewModel : BaseViewModel(
    { _, lastId, accessToken ->
        val query = SearchState.query

        ApiClient.apiService.searchItems(SearchRequest(query, lastId, accessToken))
    }
) {
    object SearchState {
        var query: String = ""
    }

    fun startSearch(query: String) {
        SearchState.query = query
    }
}
