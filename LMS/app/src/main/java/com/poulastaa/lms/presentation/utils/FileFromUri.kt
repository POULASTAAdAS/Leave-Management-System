package com.poulastaa.lms.presentation.utils

import android.content.Context
import android.net.Uri
import java.io.File

fun fileFromUri(
    context: Context,
    uri: Uri
): File? {
    return try {
        val contentResolver = context.contentResolver
        val file = File.createTempFile("profile", null, context.cacheDir)

        contentResolver.openInputStream(uri)?.use { inputStream ->
            file.outputStream().use {
                inputStream.copyTo(it)
            }
        }

        file
    } catch (e: Exception) {
        null
    }
}