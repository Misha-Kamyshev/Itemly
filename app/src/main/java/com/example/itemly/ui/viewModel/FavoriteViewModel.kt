package com.example.itemly.ui.viewModel

import com.example.itemly.data.api.ApiClient

class FavoriteViewModel : BaseViewModel(
    { lastId, accessToken ->
        ApiClient.apiService.getFavorite(lastId, "Bearer $accessToken")
    }
)
