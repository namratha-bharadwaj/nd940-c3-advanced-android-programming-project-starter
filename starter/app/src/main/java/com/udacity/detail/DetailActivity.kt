package com.udacity.detail

import android.app.DownloadManager
import android.app.NotificationManager
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.udacity.R
import com.udacity.databinding.ActivityDetailBinding
import com.udacity.main.MainActivity
import com.udacity.utils.Constants
import com.udacity.utils.Constants.Companion.EXTRA_NOTIFICATION_ID
import kotlinx.android.synthetic.main.content_detail.view.details_ok_button
import kotlinx.android.synthetic.main.content_detail.view.file_name
import kotlinx.android.synthetic.main.content_detail.view.file_status

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    private val notificationManager: NotificationManager by lazy {
        ContextCompat.getSystemService(
            this,
            NotificationManager::class.java
        ) as NotificationManager
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_detail)
        setSupportActionBar(binding.toolbar)

        val statusMessage = getStatusMessage(intent.getIntExtra(Constants.EXTRA_DOWNLOAD_STATUS, 0))
        val downloadFile = getString(intent.getIntExtra(Constants.EXTRA_DOWNLOADED_FILE, 0))
        intent.getIntExtra(EXTRA_NOTIFICATION_ID, 0)?.let {
            notificationManager.cancel(it)
        }

        setupViews(statusMessage, downloadFile)

        binding.detailContentLayout.details_ok_button.setOnClickListener {
            Intent(this, MainActivity::class.java).run {
                startActivity(this)
            }
            finish()

        }
    }

    private fun getStatusMessage(status: Int): String {
        return when(status) {
            DownloadManager.STATUS_SUCCESSFUL -> getString(R.string.download_success)
            DownloadManager.STATUS_FAILED -> getString(R.string.download_failed)
            else -> getString(R.string.download_failed)
        }

    }

    private fun setupViews(statusMessage: String, downloadedFile: String) {
        binding.detailContentLayout.file_status.text = statusMessage
        binding.detailContentLayout.file_name.text = downloadedFile
    }

}