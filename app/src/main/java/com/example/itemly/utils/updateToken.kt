package com.example.itemly.utils

import android.content.Context
import android.widget.Toast
import com.example.itemly.data.PrefKeys
import com.example.itemly.data.api.ApiClient
import com.example.itemly.data.model.CodeToken
import retrofit2.HttpException
import java.io.IOException

suspend fun updateToken(context: Context): Int {
    val pref = context.getSharedPreferences(PrefKeys.PREF_USER, Context.MODE_PRIVATE)
    val refreshToken = pref.getString(PrefKeys.REFRESH_TOKEN, "")!!

    if (refreshToken.isEmpty()) {
        logout(context)
        return CodeToken.ERROR_TOKEN
    }

    return request(context, refreshToken)
}

private suspend fun request(context: Context, refreshToken: String): Int {
    try {
        val response = ApiClient.apiService.updateToken(refreshToken)
        saveToken(context, response)

        return CodeToken.SUCCESSFUL
    } catch (e: HttpException) {
        val errorCode = e.response()?.code() ?: 500

        if (errorCode == CodeToken.ERROR_TOKEN) {
            Toast
                .makeText(context, "Войдите в акаунт снова", Toast.LENGTH_SHORT)
                .show()
            logout(context)

            return CodeToken.ERROR_TOKEN
        } else {
            Toast
                .makeText(context, "Ошибка сервера, попробуйте позже", Toast.LENGTH_LONG)
                .show()

            return CodeToken.ERROR_SERVER
        }
    } catch (e: IOException) {
        Toast
            .makeText(context, "Ошибка сети, попробуйте позже", Toast.LENGTH_LONG)
            .show()

        return CodeToken.ERROR_INTERNET
    }
}
