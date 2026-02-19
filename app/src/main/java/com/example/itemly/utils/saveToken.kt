package com.example.itemly.utils

import android.content.Context
import androidx.core.content.edit
import com.example.itemly.data.PrefKeys
import com.example.itemly.data.model.authorization.DataAuthorizationPull

fun saveToken(context: Context, response: DataAuthorizationPull) {
    val username = response.username
    val accessToken = response.accessToken
    val refreshToken = response.refreshToken

    context.getSharedPreferences(PrefKeys.PREF_USER, Context.MODE_PRIVATE).edit {
        putString(PrefKeys.USERNAME, username)
        putString(PrefKeys.ACCESS_TOKEN, accessToken)
        putString(PrefKeys.REFRESH_TOKEN, refreshToken)
        putBoolean(PrefKeys.IS_LOGIN, true)
    }
}
