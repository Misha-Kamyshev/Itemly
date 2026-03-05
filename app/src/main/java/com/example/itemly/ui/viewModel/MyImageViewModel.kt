package com.example.itemly.ui.viewModel

import com.example.itemly.data.api.ApiClient
import com.example.itemly.data.model.item.ItemsRequest

class MyImageViewModel : BaseViewModel(
    { username, lastId ->
        ApiClient.apiService.getMyImage(ItemsRequest(username, lastId))
    }
)
