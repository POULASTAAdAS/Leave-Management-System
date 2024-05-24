package com.poulastaa.lms.presentation.utils

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import java.io.File

fun fileFromUri(
    context: Context,
    uri: Uri
): File? {
    return try {
        val contentResolver = context.contentResolver
        val fileName = contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            cursor.moveToFirst()
            cursor.getString(nameIndex)
        } ?: "Profile"

        val file = File(context.cacheDir, fileName)

        contentResolver.openInputStream(uri)?.use { inputStream ->
            file.outputStream().use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }

        file
    } catch (e: Exception) {
        null
    }
}