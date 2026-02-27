package com.example.itemly.utils

import android.content.ContentUris
import android.content.Context
import android.provider.MediaStore
import com.example.itemly.data.model.addPhoto.DataPhoto

fun loadImages(context: Context): List<DataPhoto> {

    val images = mutableListOf<DataPhoto>()

    val collection = MediaStore.Files.getContentUri("external")

    val projection = arrayOf(
        MediaStore.Files.FileColumns._ID,
        MediaStore.Files.FileColumns.MEDIA_TYPE
    )

    val selection = "${MediaStore.Files.FileColumns.MEDIA_TYPE}=?"
    val selectionArgs = arrayOf(
        MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE.toString()
    )

    val sortOrder = "${MediaStore.Files.FileColumns.DATE_ADDED} DESC"

    context.contentResolver.query(
        collection,
        projection,
        selection,
        selectionArgs,
        sortOrder
    )?.use { cursor ->

        val idColumn =
            cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID)

        while (cursor.moveToNext()) {
            val id = cursor.getLong(idColumn)

            val contentUri = ContentUris.withAppendedId(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                id
            )

            images.add(DataPhoto(id, contentUri))
        }
    }

    return images
}