package com.example.itemly.ui.viewModel

import com.example.itemly.data.api.ApiClient

class AccountViewModel : BaseViewModel(
    { lastId, accessToken ->
        ApiClient.apiService.getMyImage(lastId, "Bearer $accessToken")
    }
)
