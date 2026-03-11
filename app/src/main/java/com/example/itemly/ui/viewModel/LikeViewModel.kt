package com.example.itemly.ui.viewModel

import com.example.itemly.data.api.ApiClient

class LikeViewModel : BaseViewModel(
    { lastId, accessToken ->
        ApiClient.apiService.getLike(lastId, "Bearer $accessToken")
    }
)
