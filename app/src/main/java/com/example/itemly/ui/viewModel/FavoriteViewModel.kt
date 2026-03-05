package com.example.itemly.ui.viewModel

import com.example.itemly.data.api.ApiClient
import com.example.itemly.data.model.item.ItemsRequest

class FavoriteViewModel : BaseViewModel(
    { username, lastId ->
        ApiClient.apiService.getFavorite(ItemsRequest(username, lastId))
    }
)
