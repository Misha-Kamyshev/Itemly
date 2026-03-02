package com.example.itemly.data.model.addPhoto

import android.net.Uri

data class DataPhoto(
    val id: Long,
    val uri: Uri
)

data class Album(
    val id: Long,
    val name: String,
    val coverUri: Uri,
    val photoCount: Int
)