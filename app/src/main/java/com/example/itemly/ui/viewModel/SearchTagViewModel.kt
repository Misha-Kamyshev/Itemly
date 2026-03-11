package com.example.itemly.ui.viewModel

import com.example.itemly.data.api.ApiClient
import com.example.itemly.data.model.item.SearchRequest

class SearchTagViewModel : BaseViewModel(
    { lastId, accessToken ->
        val query = SearchState.query

        ApiClient.apiService.searchItems(SearchRequest(query, lastId), "Bearer $accessToken")
    }
) {
    object SearchState {
        var query: String = ""
    }

    fun startSearch(query: String) {
        SearchState.query = query
    }
}
