package com.example.itemly.ui.viewModel

import com.example.itemly.data.api.ApiClient
import com.example.itemly.data.model.item.ItemsRequest

class AccountAuthorViewModel : BaseViewModel(
    { username, lastId ->
        ApiClient.apiService.getItemsAuthor(ItemsRequest(username, lastId))
    }
)