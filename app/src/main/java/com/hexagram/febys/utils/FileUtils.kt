package com.hexagram.febys.utils

import android.content.Context
import okhttp3.ResponseBody
import java.io.*

object FileUtils {
    fun createTempFile(context: Context): File {
        return File(context.cacheDir.path + File.separator.toString() + "feybs.pdf")
    }

    fun writeToFile(context: Context, inputStream: InputStream): File? {
        val outputStream: OutputStream?
        return try {
            val fileReader = ByteArray(4096)
            var fileSizeDownloaded: Long = 0
            val febysCartPdf = createTempFile(context)
            outputStream = FileOutputStream(febysCartPdf)
            while (true) {
                val read: Int = inputStream.read(fileReader)
                if (read == -1) {
                    break
                }
                outputStream.write(fileReader, 0, read)
                fileSizeDownloaded += read.toLong()
            }
            outputStream.flush()
            inputStream.close()
            outputStream.close()
            febysCartPdf
        } catch (e: IOException) {
            null
        }
    }

    fun writeResponseToFile(context: Context, responseBody: ResponseBody): File? {
        val inputStream: InputStream?
        inputStream = responseBody.byteStream()
        return writeToFile(context, inputStream)
    }
}