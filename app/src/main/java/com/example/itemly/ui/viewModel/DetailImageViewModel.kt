package com.example.itemly.ui.viewModel

import com.example.itemly.data.api.ApiClient
import com.example.itemly.data.model.item.ItemSimilarRequest

class DetailImageViewModel(tags: List<String>) : BaseViewModel(
    { lastId, accessToken ->
        ApiClient.apiService.getSimilar(ItemSimilarRequest(tags, lastId), "Bearer $accessToken")
    }
)
