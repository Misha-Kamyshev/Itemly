package com.example.itemly.ui.viewModel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.itemly.data.model.item.ItemData
import com.example.itemly.data.model.item.ItemsDataResponse
import com.example.itemly.data.objects.CodeToken
import com.example.itemly.data.objects.PrefKeys
import com.example.itemly.ui.components.httpToast
import com.example.itemly.ui.components.ioToast
import com.example.itemly.utils.requestWithTokenRetry
import com.example.itemly.utils.updateToken
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

abstract class BaseViewModel(
    val request: suspend (Int?, String) -> ItemsDataResponse
) : ViewModel() {
    private val _items = MutableLiveData<List<ItemData>>(emptyList())
    val items: LiveData<List<ItemData>> = _items

    private var isLoading = false
    private var isLastPage = false
    private var lastId: Int? = null

    fun loadFirstPage(context: Context) {
        if (isLoading) return

        isLastPage = false
        lastId = null
        _items.value = emptyList()

        loadPage(context)
    }

    fun loadNextPage(context: Context) {
        if (isLoading || isLastPage) return
        loadPage(context)
    }

    private fun loadPage(context: Context) {
        isLoading = true
        viewModelScope.launch {
            try {
                val response = requestWithTokenRetry(context) { token ->
                    request(lastId, token)
                }

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

    fun refresh(context: Context) {
        if (isLoading) return

        val pref = context.getSharedPreferences(PrefKeys.PREF_USER, Context.MODE_PRIVATE)
        val accessToken = pref.getString(PrefKeys.ACCESS_TOKEN, "")!!

        isLastPage = false
        lastId = null

        viewModelScope.launch {
            try {
                isLoading = true
                val response = request(lastId, accessToken)
                val newItems = response.items

                _items.value = newItems
                lastId = newItems.lastOrNull()?.id
                isLastPage = !response.hasNext
            } catch (e: HttpException) {
                if (e.code() == CodeToken.ERROR_TOKEN) {
                    updateToken(context)
                } else {
                    httpToast(context)
                }
            } catch (_: IOException) {
                ioToast(context)
            } finally {
                isLoading = false
            }
        }
    }
}
