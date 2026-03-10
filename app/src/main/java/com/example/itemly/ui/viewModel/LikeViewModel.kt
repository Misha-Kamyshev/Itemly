package com.example.itemly.ui.viewModel

import com.example.itemly.data.api.ApiClient
import com.example.itemly.data.model.item.ItemsRequest

class LikeViewModel : BaseViewModel(
    { username, lastId ->
        ApiClient.apiService.getLike(ItemsRequest(username, lastId))
    }
)
