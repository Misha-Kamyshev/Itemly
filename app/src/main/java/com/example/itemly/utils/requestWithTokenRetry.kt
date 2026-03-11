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
    var accessToken = pref.getString(PrefKeys.ACCESS_TOKEN, "")!!

    try {
        return request(accessToken)

    } catch (e: HttpException) {
        if (e.code() == CodeToken.ERROR_TOKEN) {
            val result = updateToken(context)

            if (result == CodeToken.SUCCESSFUL) {
                accessToken = pref.getString(PrefKeys.ACCESS_TOKEN, "")!!
                return request(accessToken)
            }
        }

        throw e
    }
}
