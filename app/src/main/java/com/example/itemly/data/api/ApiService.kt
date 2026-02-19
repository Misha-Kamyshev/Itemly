package com.example.itemly.data.api

import com.example.itemly.data.model.authorization.DataAuthorizationPull
import com.example.itemly.data.model.authorization.DataAuthorizationPush
import com.example.itemly.data.model.authorization.DataRegistrationPush
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("user/signin")
    suspend fun signIn(@Body request: DataAuthorizationPush): DataAuthorizationPull

    @POST("user/signup")
    suspend fun signUp(@Body request: DataRegistrationPush): DataAuthorizationPull
}
