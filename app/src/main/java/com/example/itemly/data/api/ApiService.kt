package com.example.itemly.data.api

import com.example.itemly.data.model.authorization.DataAuthorizationPull
import com.example.itemly.data.model.authorization.DataAuthorizationPush
import com.example.itemly.data.model.authorization.DataRegistrationPush
import com.example.itemly.data.model.item.ItemInformation
import com.example.itemly.data.model.item.ItemRequest
import com.example.itemly.data.model.item.ItemsData
import com.example.itemly.data.model.item.ItemsRequest
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface ApiService {
    @POST("user/signin")
    suspend fun signIn(@Body request: DataAuthorizationPush): DataAuthorizationPull

    @POST("user/signup")
    suspend fun signUp(@Body request: DataRegistrationPush): DataAuthorizationPull

    @POST("user/update_token")
    suspend fun updateToken(@Body refreshToken: String): DataAuthorizationPull

    @POST("items/get_main")
    suspend fun getMain(@Body request: ItemsRequest): ItemsData

    @GET("items/get_information")
    suspend fun getInformation(@Query("id_item") id: Int): ItemInformation // TODO Переделать эндпоинт на бэке

    @POST("item/add_like")
    suspend fun addLike(@Body request: ItemRequest): Response<Unit> // TODO Сделать эндпоинт

    @POST("item/delete_like")
    suspend fun deleteLike(@Body request: ItemRequest): Response<Unit> // TODO Сделать эндпоинт

    @Multipart
    @POST("/items/add_item")
    suspend fun addItem(
        @Part("username") username: RequestBody,
        @Part("name_item") nameItem: RequestBody,
        @Part("tags") tags: RequestBody,
        @Part image: MultipartBody.Part
    ): Response<Unit>

    @POST("/items/get_favorite")
    suspend fun getFavorite(@Body request: ItemsRequest): ItemsData

    @POST("/items/get_my_image")
    suspend fun getMyImage(@Body request: ItemsRequest): ItemsData

    @POST("/items/save_item")
    suspend fun saveItem(@Body request: ItemRequest): Response<Unit>

    @POST("/item/delete_favorite_item")
    suspend fun deleteFavoriteItem(@Body request: ItemRequest): Response<Unit>
}
