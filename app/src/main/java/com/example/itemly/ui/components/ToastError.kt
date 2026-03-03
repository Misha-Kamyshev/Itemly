package com.example.itemly.ui.components

import android.content.Context
import android.widget.Toast
import org.json.JSONObject
import retrofit2.HttpException

fun httpToast(e: HttpException, context: Context?) {
    val errorJson = e.response()?.errorBody()?.string()
        ?: "{\"detail\": \"Ошибка сервера\"}"
    val detail = JSONObject(errorJson).getString("detail")
    Toast.makeText(
        context,
        detail,
        Toast.LENGTH_LONG
    ).show()
}

fun ioToast(context: Context?) {
    Toast.makeText(
        context,
        "Ошибка сети попробуйте позже",
        Toast.LENGTH_LONG
    ).show()
}