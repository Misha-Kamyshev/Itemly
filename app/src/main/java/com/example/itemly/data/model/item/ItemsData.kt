package com.example.itemly.data.model.item

import com.google.gson.annotations.SerializedName
import retrofit2.http.Query
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
    val lastId: Int? = null
)

data class ItemRequest(
    val id: Int,
    val username: String
)

data class ItemInformation(
    val tags: List<String>,
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
    val lastId: Int? = null
)

data class SearchRequest(
    val query: String,
    val lastId: Int?
)
