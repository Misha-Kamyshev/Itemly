package com.example.itemly.data.api

import com.example.itemly.data.model.authorization.DataAuthorizationPull
import com.example.itemly.data.model.authorization.DataAuthorizationPush
import com.example.itemly.data.model.authorization.DataRegistrationPush
import com.example.itemly.data.model.home.HomeData
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    @POST("user/signin")
    suspend fun signIn(@Body request: DataAuthorizationPush): DataAuthorizationPull

    @POST("user/signup")
    suspend fun signUp(@Body request: DataRegistrationPush): DataAuthorizationPull

    @POST("user/update_token")
    suspend fun updateToken(@Body refreshToken: String): DataAuthorizationPull

    @POST("image/get_main")
    suspend fun getMain(@Body username: String?): HomeData
}
