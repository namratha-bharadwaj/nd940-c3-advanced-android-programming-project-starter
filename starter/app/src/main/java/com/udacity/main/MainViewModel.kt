package com.udacity.main

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.udacity.R
import com.udacity.model.*

class MainViewModel(private val app: Application): ViewModel() {

    private var downloadID: Long = 0

    private var _checkedUrl: DownloadUrls? = null

    private var _downloadInfo = MutableLiveData<DownloadInfo>()
    val downloadInfo: MutableLiveData<DownloadInfo>
        get() = _downloadInfo

    private var _message = MutableLiveData<Message>()
    val message: MutableLiveData<Message>
        get() = _message

    fun selectedUrl(url: DownloadUrls) {
        _checkedUrl = url
    }

    fun onDownloadButtonClick() {
        if (_checkedUrl == null) {
            _message.value = ToastMessage(app.getString(R.string.no_option_selected_toast))
        } else {
            _downloadInfo.value = DownloadInfo(
                _checkedUrl!!,
                app.getString(R.string.app_name),
                app.getString(R.string.app_description)
            )
        }
    }

    fun messageShown() {
        _message.value = null
    }

    fun download(downloadInfo: DownloadInfo) {
        val request =
            DownloadManager.Request(Uri.parse(downloadInfo.downloadUrl.url))
                .setTitle(downloadInfo.notificationTitle)
                .setDescription(downloadInfo.notificationDescription)
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

        val downloadManager = app.getSystemService(AppCompatActivity.DOWNLOAD_SERVICE) as DownloadManager
        downloadID = downloadManager.enqueue(request)// enqueue puts the download request in the queue.
    }

    val receiver = object: BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            val status = getDownloadStatus(id!!)
            onDownloadComplete(id, status)
        }

    }

    private fun getDownloadStatus(id: Long): Int {
        val downloadManager = app.getSystemService(AppCompatActivity.DOWNLOAD_SERVICE) as DownloadManager
        val query = DownloadManager.Query()
        query.setFilterById(id)
        val cursor = downloadManager.query(query)
        var status = -1
        if (cursor.moveToFirst()) {
            status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
        }
        cursor.close()
        return status
    }

    fun onDownloadComplete(id: Long, downloadStatus: Int) {
        _message.value = NotificationMessage(
            id = id.toInt(),
            title = app.getString(R.string.notification_title),
            description = app.getString(R.string.notification_description),
            channelID = app.getString(R.string.channel_id),
            channelName = app.getString(R.string.channel_name),
            downloadStatus = downloadStatus,
            actionText = app.getString(R.string.action_text),
            downloadUrl = _downloadInfo.value!!.downloadUrl
        )
    }
}