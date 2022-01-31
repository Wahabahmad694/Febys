package com.hexagram.febys.utils

import android.app.DownloadManager
import android.content.Context
import android.database.ContentObserver
import android.net.Uri
import android.os.Environment
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.hexagram.febys.base.BaseViewModel
import com.hexagram.febys.network.DataState
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import javax.inject.Inject

class AppUtils @Inject constructor(
    @ApplicationContext private val context: Context
) : BaseViewModel() {

    // get all properties
    private val _downloadPdf = MutableLiveData<DataState<ResponseBody>>()
    val observerDownloadPdf: LiveData<DataState<ResponseBody>> = _downloadPdf


    fun startMessageMediaDownload(url: String) = viewModelScope.launch {
//        updateMessageMediaDownloadStatus(messageIndex, DownloadState.DOWNLOADING)

        val sourceUri = Uri.parse(url.trim())
//        val sourceUri = sourceUriResult.getOrElse { e ->
//            Timber.w(e, "Message media download failed: cannot get sourceUri")
////            updateMessageMediaDownloadStatus(messageIndex, DownloadState.ERROR)
//            return@launch
//        }

        val downloadManager =
            context.getSystemService(AppCompatActivity.DOWNLOAD_SERVICE) as DownloadManager
        val downloadRequest = DownloadManager.Request(sourceUri).apply {
            setDestinationInExternalFilesDir(
                context,
                Environment.DIRECTORY_DOWNLOADS,
                sourceUri.pathSegments.last()
            )
            setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        }
        val downloadId = downloadManager.enqueue(downloadRequest)
        observeMessageMediaDownload(downloadId)
    }

    private fun observeMessageMediaDownload(downloadId: Long) {
        val downloadManager =
            context.getSystemService(AppCompatActivity.DOWNLOAD_SERVICE) as DownloadManager
        val downloadCursor = downloadManager.queryById(downloadId)
        val downloadObserver = object : ContentObserver(Handler(Looper.getMainLooper())) {
            override fun onChange(selfChange: Boolean) {
                if (!updateMessageMediaDownloadState(downloadId)) {
                    downloadCursor.unregisterContentObserver(this)
                    downloadCursor.close()
                }
            }
        }
        downloadCursor.registerContentObserver(downloadObserver)
    }

    /**
     * Notifies the view model of the current download state
     * @return true if the download is still in progress
     */
    private fun updateMessageMediaDownloadState(downloadId: Long): Boolean {
        val downloadManager =
            context.getSystemService(AppCompatActivity.DOWNLOAD_SERVICE) as DownloadManager
        val cursor = downloadManager.queryById(downloadId)

        if (!cursor.moveToFirst()) {
            cursor.close()
            return false
        }

        val status = cursor.getInt(DownloadManager.COLUMN_STATUS)
        val downloadInProgress =
            status != DownloadManager.STATUS_FAILED && status != DownloadManager.STATUS_SUCCESSFUL
        val downloadedBytes = cursor.getLong(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR)
        _downloadPdf.postValue(_downloadPdf.value)

        cursor.close()
        return downloadInProgress
    }
}
