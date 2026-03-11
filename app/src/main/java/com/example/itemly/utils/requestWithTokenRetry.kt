package com.example.itemly.utils

import android.content.Context
import com.example.itemly.data.objects.CodeToken
import com.example.itemly.data.objects.PrefKeys
import retrofit2.HttpException

suspend fun <T> requestWithTokenRetry(
    context: Context,
    request: suspend (String) -> T
): T {
    val pref = context.getSharedPreferences(PrefKeys.PREF_USER, Context.MODE_PRIVATE)
    val accessToken = pref.getString(PrefKeys.ACCESS_TOKEN, null)
        ?: throw Exception("No access token")

    try {
        return request(accessToken)

    } catch (e: HttpException) {

        if (e.code() != 401)
            throw e

        val result = updateToken(context)

        if (result != CodeToken.SUCCESSFUL)
            throw e

        val newToken = pref.getString(PrefKeys.ACCESS_TOKEN, null)
            ?: throw Exception("Token refresh failed")

        return request(newToken)
    }
}
