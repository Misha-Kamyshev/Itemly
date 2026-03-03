package com.example.itemly.ui.viewModel

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.itemly.data.api.ApiClient
import com.example.itemly.data.model.item.ItemsRequest
import com.example.itemly.data.model.item.ItemDataSchema
import com.example.itemly.ui.components.httpToast
import com.example.itemly.ui.components.ioToast
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException

class HomeViewModel : ViewModel() {
    private val _items = MutableLiveData<List<ItemDataSchema>>(emptyList())
    val items: LiveData<List<ItemDataSchema>> = _items

    private var isLoading = false
    private var isLastPage = false
    private var lastId: Int? = null

    fun loadFirstPage(username: String, context: Context) {
        if (_items.value!!.isNotEmpty()) return
        loadPage(username, context)
    }

    fun loadNextPage(username: String, context: Context) {
        if (isLoading || isLastPage) return
        loadPage(username, context)
    }

    private fun loadPage(username: String, context: Context) {
        isLoading = true

        viewModelScope.launch {
            try {
                val response = ApiClient.apiService.getMain(ItemsRequest(username, lastId))

                val newItems = response.items

                if (newItems.isEmpty()) {
                    isLastPage = true
                } else {
                    val updatedList = _items.value!! + newItems
                    _items.value = updatedList
                    lastId = newItems.last().id
                    isLastPage = !response.hasNext
                }
            } catch (_: HttpException) {
                httpToast(context)
            } catch (_: IOException) {
                ioToast(context)
            } finally {
                isLoading = false
            }
        }
    }
}
