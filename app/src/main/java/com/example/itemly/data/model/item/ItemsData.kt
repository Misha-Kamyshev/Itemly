package com.example.itemly.data.model.item

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ItemData(
    val id: Int,
    @SerializedName("image_url")
    val imageUrl: String
) : Serializable

data class ItemsDataResponse(
    val items: List<ItemData>,
    @SerializedName("has_next")
    val hasNext: Boolean
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
    val tags: List<String>,
    @SerializedName("last_id")
    val lastId: Int? = null
)

data class SearchRequest(
    val query: String,
    @SerializedName("last_id")
    val lastId: Int?
)
