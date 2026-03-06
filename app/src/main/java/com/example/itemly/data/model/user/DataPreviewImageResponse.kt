package com.example.itemly.data.model.user

import com.google.gson.annotations.SerializedName

data class DataPreviewImageResponse(
    @SerializedName("path_preview")
    val pathPreview: String?
)