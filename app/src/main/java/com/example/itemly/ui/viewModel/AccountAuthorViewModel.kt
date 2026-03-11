package com.example.itemly.ui.viewModel

import com.example.itemly.data.api.ApiClient

class AccountAuthorViewModel : BaseViewModel(
    { lastId, accessToken ->
        ApiClient.apiService.getItemsAuthor(lastId, "Bearer $accessToken")
    }
)
