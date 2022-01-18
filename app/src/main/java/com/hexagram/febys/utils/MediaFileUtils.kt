package com.hexagram.febys.utils

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

 object MediaFileUtils {
    fun handleUri(context: Context, uri: Uri): String? {
        context.apply {
            val type = when (contentResolver.getType(uri)) {
                "image/jpeg" -> ".jpg"
                //another types
                else -> ".png"
            }
            val dir = File(getExternalFilesDir("febys"), "dir_name").apply { mkdir() }
            val fileToSave = File(dir, "${System.currentTimeMillis()}$type")
            copyStreamToFile(contentResolver.openInputStream(uri)!!, fileToSave)
            if (fileToSave.exists()) {
                return fileToSave.absolutePath
            }
            return ""
        }
    }

    private fun copyStreamToFile(inputStream: InputStream, outputFile: File) {
        inputStream.use { input ->
            val outputStream = FileOutputStream(outputFile)
            outputStream.use { output ->
                val buffer = ByteArray(4 * 1024) // buffer size
                while (true) {
                    val byteCount = input.read(buffer)
                    if (byteCount < 0) break
                    output.write(buffer, 0, byteCount)
                }
                output.flush()
            }
        }
    }
}