package com.example.itemly.utils

import android.content.ContentUris
import android.content.Context
import android.provider.MediaStore
import com.example.itemly.data.model.addPhoto.Album
import com.example.itemly.data.model.addPhoto.DataPhoto

fun loadAlbums(context: Context): List<Album> {

    val albums = mutableListOf<Album>()

    val collection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI

    val projection = arrayOf(
        MediaStore.Images.Media.BUCKET_ID,
        MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
        MediaStore.Images.Media._ID
    )

    val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"

    val albumMap = mutableMapOf<Long, Pair<String?, MutableList<Long>>>()

    context.contentResolver.query(
        collection,
        projection,
        null,
        null,
        sortOrder
    )?.use { cursor ->

        val bucketIdColumn =
            cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_ID)

        val bucketNameColumn =
            cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)

        val idColumn =
            cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)

        while (cursor.moveToNext()) {

            val bucketId = cursor.getLong(bucketIdColumn)
            val bucketName = cursor.getString(bucketNameColumn)
            val imageId = cursor.getLong(idColumn)

            val entry = albumMap.getOrPut(bucketId) {
                bucketName to mutableListOf()
            }

            entry.second.add(imageId)
        }
    }

    albumMap.forEach { (bucketId, pair) ->

        val bucketName = pair.first ?: "Unknown"
        val imageIds = pair.second

        if (imageIds.isNotEmpty()) {

            val firstImageId = imageIds.first()

            val coverUri = ContentUris.withAppendedId(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                firstImageId
            )

            albums.add(
                Album(
                    id = bucketId,
                    name = bucketName,
                    coverUri = coverUri,
                    photoCount = imageIds.size
                )
            )
        }
    }

    return albums
}

fun loadImagesFromAlbum(context: Context, bucketId: Long): List<DataPhoto> {

    val images = mutableListOf<DataPhoto>()

    val collection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI

    val projection = arrayOf(
        MediaStore.Images.Media._ID
    )

    val selection = "${MediaStore.Images.Media.BUCKET_ID}=?"
    val selectionArgs = arrayOf(bucketId.toString())

    val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"

    context.contentResolver.query(
        collection,
        projection,
        selection,
        selectionArgs,
        sortOrder
    )?.use { cursor ->

        val idColumn =
            cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)

        while (cursor.moveToNext()) {
            val id = cursor.getLong(idColumn)

            val uri = ContentUris.withAppendedId(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                id
            )

            images.add(DataPhoto(id, uri))
        }
    }

    return images
}