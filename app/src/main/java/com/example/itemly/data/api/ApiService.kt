package com.example.itemly.data.api

import com.example.itemly.data.model.authorization.DataAuthorizationPull
import com.example.itemly.data.model.authorization.DataAuthorizationPush
import com.example.itemly.data.model.authorization.DataRegistrationPush
import com.example.itemly.data.model.item.ItemInformationResponse
import com.example.itemly.data.model.item.ItemSimilarRequest
import com.example.itemly.data.model.item.ItemsDataResponse
import com.example.itemly.data.model.item.SearchRequest
import com.example.itemly.data.model.user.DataPreviewImageResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface ApiService {
    @POST("user/signin")
    suspend fun signIn(@Body request: DataAuthorizationPush): DataAuthorizationPull

    @POST("user/signup")
    suspend fun signUp(@Body request: DataRegistrationPush): DataAuthorizationPull

    @Multipart
    @POST("user/change_preview")
    suspend fun changePreview(
        @Part image: MultipartBody.Part,
        @Header("Authorization") token: String
    ): Response<Unit>

    @POST("user/get_image_user")
    suspend fun getImageUser(@Query("username") username: String): DataPreviewImageResponse

    @POST("user/update_token")
    suspend fun updateToken(@Body refreshToken: String): DataAuthorizationPull

    @POST("items/get_main")
    suspend fun getMain(
        @Query("last_id") lastId: Int?,
        @Header("Authorization") token: String
    ): ItemsDataResponse

    @GET("items/get_information")
    suspend fun getInformation(
        @Query("item_id") itemId: Int,
        @Header("Authorization") token: String
    ): ItemInformationResponse

    @POST("items/add_like")
    suspend fun addLike(
        @Query("item_id") itemId: Int,
        @Header("Authorization") token: String
    ): Response<Unit>

    @POST("items/delete_like")
    suspend fun deleteLike(
        @Query("item_id") itemId: Int,
        @Header("Authorization") token: String
    ): Response<Unit>

    @Multipart
    @POST("items/add_item")
    suspend fun addItem(
        @Part("name_item") nameItem: RequestBody,
        @Part("tags") tags: RequestBody,
        @Part image: MultipartBody.Part,
        @Header("Authorization") token: String
    ): Response<Unit>

    @POST("items/get_favorite")
    suspend fun getFavorite(
        @Query("last_id") lastId: Int?,
        @Header("Authorization") token: String
    ): ItemsDataResponse

    @POST("items/get_my_image")
    suspend fun getMyImage(
        @Query("last_id") lastId: Int?,
        @Header("Authorization") token: String
    ): ItemsDataResponse

    @POST("items/get_like")
    suspend fun getLike(
        @Query("last_id") lastId: Int?,
        @Header("Authorization") token: String
    ): ItemsDataResponse

    @POST("items/save_item")
    suspend fun saveItem(
        @Query("item_id") itemId: Int,
        @Header("Authorization") token: String
    ): Response<Unit>

    @POST("items/delete_favorite_item")
    suspend fun deleteFavoriteItem(
        @Query("item_id") itemId: Int,
        @Header("Authorization") token: String
    ): Response<Unit>

    @POST("items/get_similar_images")
    suspend fun getSimilar(
        @Body request: ItemSimilarRequest,
        @Header("Authorization") token: String
    ): ItemsDataResponse

    @POST("items/get_items_author")
    suspend fun getItemsAuthor(
        @Query("last_id") lastId: Int?,
        @Header("Authorization") token: String
    ): ItemsDataResponse

    @POST("items/delete_item")
    suspend fun deleteItem(
        @Query("item_id") itemId: Int,
        @Header("Authorization") token: String
    ): Response<Unit>

    @POST("items/search_items")
    suspend fun searchItems(
        @Body request: SearchRequest,
        @Header("Authorization") token: String
    ): ItemsDataResponse
}

