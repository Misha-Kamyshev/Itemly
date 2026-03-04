package com.example.itemly.utils

import android.content.Context
import androidx.core.content.edit
import com.example.itemly.data.objects.PrefKeys


fun logout(context: Context) {
    context.getSharedPreferences(PrefKeys.PREF_USER, Context.MODE_PRIVATE).edit {
        putString(PrefKeys.USERNAME, null)
        putString(PrefKeys.E_MAIL, null)
        putString(PrefKeys.ACCESS_TOKEN, null)
        putString(PrefKeys.REFRESH_TOKEN, null)
        putBoolean(PrefKeys.IS_LOGIN, false)
    }
}
