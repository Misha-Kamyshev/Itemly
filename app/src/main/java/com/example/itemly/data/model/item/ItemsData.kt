package com.example.itemly.data.model.item

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ItemDataSchema(
    val id: Int,
    @SerializedName("image_url")
    val imageUrl: String
) : Serializable

data class ItemsData(
    val items: List<ItemDataSchema>,
    @SerializedName("has_next")
    val hasNext: Boolean
)

data class ItemsRequest(
    val username: String,
    @SerializedName("last_id")
    val lastId: Int? = null,
    @SerializedName("access_token")
    val accessToken: String
)

data class ItemRequest(
    val id: Int,
    val username: String,
    @SerializedName("access_token")
    val accessToken: String
)

data class ItemInformationResponse(
    val tags: List<String>,
    @SerializedName("icon_author")
    val iconAuthor: String?,
    val author: String,
    val name: String,
    @SerializedName("count_like")
    val countLike: Int = 0,
    @SerializedName("save_item")
    val saveItem: Boolean,
    @SerializedName("like_item")
    val likeItem: Boolean
)

data class ItemSimilarRequest(
    val username: String,
    val tags: List<String>,
    @SerializedName("last_id")
    val lastId: Int? = null,
    @SerializedName("access_token")
    val accessToken: String
)

data class SearchRequest(
    val query: String,
    @SerializedName("last_id")
    val lastId: Int?,
    @SerializedName("access_token")
    val accessToken: String
)

data class ItemInformationRequest(
    val id: Int,
    val username: String,
    @SerializedName("access_token")
    val accessToken: String
)

data class AccessTokenRequest(
    @SerializedName("access_token")
    val accessToken: String
)
