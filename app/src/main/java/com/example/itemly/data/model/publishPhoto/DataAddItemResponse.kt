package com.example.itemly.data.model.publishPhoto

import com.google.gson.annotations.SerializedName

data class DataAddItemResponse(
    @SerializedName("item_id")
    val itemId: Int,
    val tags: List<String>,
    @SerializedName("image_url")
    val imageUrl: String
)
