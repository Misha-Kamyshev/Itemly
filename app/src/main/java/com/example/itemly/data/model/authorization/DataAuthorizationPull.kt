package com.example.itemly.data.model.authorization

import com.google.gson.annotations.SerializedName

data class DataAuthorizationPull(
    val username: String,
    val email: String,
    @SerializedName("access_token")
    val accessToken: String,
    @SerializedName("refresh_token")
    val refreshToken: String
)