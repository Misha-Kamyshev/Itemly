package com.example.itemly.data.model.publishPhoto

import kotlinx.serialization.SerialName

data class DataAddItemResponse(
    @SerialName("item_id")
    val itemId: Int,
    val tags: List<String>,
    @SerialName("image_url")
    val imageUrl: String
)
