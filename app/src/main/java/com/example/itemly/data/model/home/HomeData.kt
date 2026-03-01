package com.example.itemly.data.model.home

import kotlinx.serialization.SerialName

data class ItemDataSchema(
    val id: Int,
    val imageUrl: String
)

data class HomeData(
    val items: List<ItemDataSchema>,
    @SerialName("has_next")
    val hasNext: Boolean
)

data class HomeRequest(
    val username: String,
    val last_id: Int? = null
)