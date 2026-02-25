package com.example.itemly.ui.viewModel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.itemly.data.api.ApiClient
import com.example.itemly.data.model.home.HomeData
import com.example.itemly.data.objects.PrefKeys
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    private val _homeData = MutableLiveData<HomeData>()
    val homeData: LiveData<HomeData> = _homeData

    fun loadData(context: Context) {
        if (_homeData.value != null) return

        val pref = context.getSharedPreferences(PrefKeys.USERNAME, Context.MODE_PRIVATE)
        val username = pref.getString(PrefKeys.USERNAME, null)

        viewModelScope.launch {
            val imageList = runCatching {
                ApiClient.apiService.getMain(username)
            }.getOrNull()
        }
    }
}