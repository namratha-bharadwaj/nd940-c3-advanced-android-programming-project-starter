package com.udacity.model

import androidx.annotation.StringRes
import com.udacity.R

enum class DownloadUrls(val url: String, @StringRes val downloadRepoTitle: Int) {
    GLIDE("https://github.com/bumptech/glide/archive/master.zip", R.string.glide_text),
    UDACITY("https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip", R.string.udacity_text),
    RETROFIT("https://github.com/square/retrofit/archive/master.zip", R.string.retrofit_text)
}

data class DownloadInfo(
    val downloadUrl: DownloadUrls,
    val notificationTitle: String,
    val notificationDescription: String
)

sealed class Message
data class ToastMessage(
    val messageContent: String
) : Message()
data class NotificationMessage(
    val id: Int,
    val title: String,
    val description: String,
    val channelID: String,
    val channelName: String,
    val downloadStatus: Int,
    val actionText: String,
    val downloadUrl: DownloadUrls
): Message()
