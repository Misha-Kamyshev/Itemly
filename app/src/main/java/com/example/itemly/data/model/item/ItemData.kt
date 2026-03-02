package com.example.itemly.data.model.item

import com.google.gson.annotations.SerializedName


data class ItemDataSchema(
    val id: Int,
    @SerializedName("image_url")
    val imageUrl: String
)

data class ItemData(
    val items: List<ItemDataSchema>,
    @SerializedName("has_next")
    val hasNext: Boolean
)

data class ItemRequest(
    val username: String,
    val last_id: Int? = null
)