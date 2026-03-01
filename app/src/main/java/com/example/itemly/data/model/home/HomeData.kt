package com.example.itemly.data.model.home

import com.google.gson.annotations.SerializedName


data class ItemDataSchema(
    val id: Int,
    @SerializedName("image_url")
    val imageUrl: String
)

data class HomeData(
    val items: List<ItemDataSchema>,
    @SerializedName("has_next")
    val hasNext: Boolean
)

data class HomeRequest(
    val username: String,
    val last_id: Int? = null
)