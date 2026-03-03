package com.example.itemly.data.model.item

import com.google.gson.annotations.SerializedName


data class ItemDataSchema(
    val id: Int,
    @SerializedName("image_url")
    val imageUrl: String
)

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
