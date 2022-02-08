package com.hexagram.febys.utils

import android.content.Context
import android.net.Uri
import android.provider.DocumentsContract
import okhttp3.ResponseBody
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

object FileUtils {
    @Throws
    fun createUri(context: Context, directory: Uri, mimeType: String, name: String): Uri {
        val docId = DocumentsContract.getTreeDocumentId(directory)
        val dirUri = DocumentsContract.buildDocumentUriUsingTree(directory, docId)
        return DocumentsContract.createDocument(
            context.contentResolver, dirUri, mimeType, name
        ) ?: throw FileNotFoundException()
    }

    fun writeToFile(
        context: Context, directory: Uri, mimeType: String, name: String, inputStream: InputStream
    ): Uri? {
        return try {
            val uri = createUri(context, directory, mimeType, name)
            context.contentResolver.openFileDescriptor(uri, "w")?.use {
                FileOutputStream(it.fileDescriptor).use { outputStream ->
                    outputStream.write(inputStream.readBytes())
                }
            }
            uri
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            null
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    fun writeResponseToFile(
        context: Context, directory: Uri, mimeType: String, name: String, responseBody: ResponseBody
    ): Uri? {
        val inputStream = responseBody.byteStream()
        return writeToFile(context, directory, mimeType, name, inputStream)
    }
}