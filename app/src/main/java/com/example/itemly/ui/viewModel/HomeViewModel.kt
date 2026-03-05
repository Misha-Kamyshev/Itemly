package com.example.itemly.ui.viewModel

import com.example.itemly.data.api.ApiClient
import com.example.itemly.data.model.item.ItemsRequest

class HomeViewModel : BaseViewModel(
    { username, lastId ->
        ApiClient.apiService.getMain(ItemsRequest(username, lastId))
    }
)
