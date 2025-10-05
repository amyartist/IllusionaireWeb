package com.amyartist.illusionaireapp.utils

import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

suspend fun findImageUriByName(context: Context, imageDisplayName: String): Uri? {
    return withContext(Dispatchers.IO) {
        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME
        )
        val selection = "${MediaStore.Images.Media.DISPLAY_NAME} = ?"
        val selectionArgs = arrayOf("$imageDisplayName.jpg")

        val sortOrder = "${MediaStore.Images.Media.DATE_TAKEN} DESC"

        var imageUri: Uri? = null

        try {
            context.contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                sortOrder
            )?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                    val id = cursor.getLong(idColumn)
                    imageUri = ContentUris.withAppendedId(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        id
                    )
                }
            }
        } catch (e: Exception) {
            Log.e("ImageSearch", "Error finding image by name: $imageDisplayName", e)
        }
        imageUri
    }
}

fun saveBitmapToGalleryWithSpecificName(context: Context, bitmap: Bitmap, displayName: String): Boolean {
    val imageCollection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
    } else {
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
    }

    val contentValues = ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME, "$displayName.jpg")
        put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            put(MediaStore.Images.Media.IS_PENDING, 1)
        }
    }

    val resolver = context.contentResolver
    val uri = resolver.insert(imageCollection, contentValues)

    uri?.let {
        try {
            resolver.openOutputStream(it).use { outputStream ->
                if (outputStream == null) throw IOException("Failed to get output stream.")
                if (!bitmap.compress(Bitmap.CompressFormat.JPEG, 95, outputStream)) {
                    throw IOException("Failed to save bitmap.")
                }
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                contentValues.clear()
                contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
                resolver.update(it, contentValues, null, null)
            }
            Log.d("ImageSave", "Image saved to gallery: $displayName.jpg")
            return true
        } catch (e: Exception) {
            Log.e("ImageSave", "Error saving bitmap to gallery: $displayName", e)
            resolver.delete(it, null, null) // Clean up entry if error
            return false
        }
    }
    return false
}
