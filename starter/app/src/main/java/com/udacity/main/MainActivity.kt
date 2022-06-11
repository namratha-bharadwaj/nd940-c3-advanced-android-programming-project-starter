package com.udacity.main

import android.app.*
import android.content.*
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.udacity.R
import com.udacity.databinding.ActivityMainBinding
import com.udacity.model.NotificationMessage
import com.udacity.model.ToastMessage
import com.udacity.utils.cancelNotifications
import com.udacity.utils.showNotification


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val viewModel: MainViewModel by lazy {
        val activity = requireNotNull(this)

        ViewModelProvider(
            this,
            MainViewModelFactory(activity.application)
        ).get(MainViewModel::class.java)
    }

    private val notificationManager: NotificationManager by lazy {
        ContextCompat.getSystemService(
            this,
            NotificationManager::class.java
        ) as NotificationManager
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this
        setSupportActionBar(binding.toolbar)

        binding.mainContentLayout.viewModel = viewModel

        viewModel.message.observe(this, Observer {
            it?.let { message ->
                when(message) {
                    is ToastMessage -> showToast(message)
                    is NotificationMessage -> notificationManager.showNotification(
                        message, this,
                        notificationManager
                    )
                }
                viewModel.messageShown()
            }
        })

        viewModel.downloadInfo.observe(this, Observer { downloadInfo ->
            downloadInfo?.let {
                notificationManager.cancelNotifications()
                viewModel.download(it)
                Handler().postDelayed({
                    viewModel.onDownloadComplete(1, 1)
                }, 3000)
            }
        })

        registerReceiver(viewModel.receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
    }

    private fun showToast(toastMessage: ToastMessage) {
        Toast.makeText(this, toastMessage.messageContent, Toast.LENGTH_SHORT).show()
    }

}
