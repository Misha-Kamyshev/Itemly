package com.example.itemly.ui.viewModel

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.itemly.data.model.item.ItemDataSchema
import com.example.itemly.data.model.item.ItemsData
import com.example.itemly.data.objects.CodeToken
import com.example.itemly.data.objects.PrefKeys
import com.example.itemly.ui.components.httpToast
import com.example.itemly.ui.components.ioToast
import com.example.itemly.utils.updateToken
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

abstract class BaseViewModel(
    val request: suspend (String, Int?, String) -> ItemsData
) : ViewModel() {
    private val _items = MutableLiveData<List<ItemDataSchema>>(emptyList())
    val items: LiveData<List<ItemDataSchema>> = _items

    private var isLoading = false
    private var isLastPage = false
    private var lastId: Int? = null

    fun loadFirstPage(username: String, context: Context) {
        if (isLoading) return

        isLastPage = false
        lastId = null
        _items.value = emptyList()

        loadPage(username, context)
    }

    fun loadNextPage(username: String, context: Context) {
        if (isLoading || isLastPage) return
        loadPage(username, context)
    }

    private fun loadPage(username: String, context: Context) {
        isLoading = true
        val pref = context.getSharedPreferences(PrefKeys.USERNAME, Context.MODE_PRIVATE)
        val accessToken = pref.getString(PrefKeys.ACCESS_TOKEN, "")!!

        viewModelScope.launch {
            try {
                val response = request(username, lastId, accessToken)

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

    fun refresh(username: String, context: Context) {
        if (isLoading) return

        val pref = context.getSharedPreferences(PrefKeys.USERNAME, Context.MODE_PRIVATE)
        val accessToken = pref.getString(PrefKeys.ACCESS_TOKEN, "")!!

        isLastPage = false
        lastId = null

        viewModelScope.launch {
            try {
                isLoading = true
                val response = request(username, null, accessToken)
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
