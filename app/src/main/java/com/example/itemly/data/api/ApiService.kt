package com.example.itemly.data.api

import com.example.itemly.data.model.authorization.DataAuthorizationPull
import com.example.itemly.data.model.authorization.DataAuthorizationPush
import com.example.itemly.data.model.authorization.DataRegistrationPush
import com.example.itemly.data.model.home.HomeData
import com.example.itemly.data.model.publishPhoto.DataAddItemResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService {
    @POST("user/signin")
    suspend fun signIn(@Body request: DataAuthorizationPush): DataAuthorizationPull

    @POST("user/signup")
    suspend fun signUp(@Body request: DataRegistrationPush): DataAuthorizationPull

    @POST("user/update_token")
    suspend fun updateToken(@Body refreshToken: String): DataAuthorizationPull

    @POST("items/get_main")
    suspend fun getMain(@Body username: String?): HomeData

    @Multipart
    @POST("/items/add_item")
    suspend fun addItem(
        @Part("username") username: RequestBody,
        @Part("name_item") nameItem: RequestBody,
        @Part("tags") tags: RequestBody,
        @Part image: MultipartBody.Part
    ): DataAddItemResponse
}
