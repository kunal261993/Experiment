package com.example.loadapp

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.Cursor
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.loadapp.databinding.ActivityMainBinding
import java.io.File

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var downloadID: Long = 0
    private lateinit var notificationManager: NotificationManager
    var downloadStatus = ""
    var fileName = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        binding.apply {
            mainButtons.customButton.setOnClickListener {
                if (mainButtons.radioGlide.isChecked) {
                    fileName = applicationContext.getString(R.string.glide)
                    mainButtons.customButton.setState(ButtonState.Loading)
                    download(URL_GLIDE)
                } else if (mainButtons.radioUdacity.isChecked) {
                    fileName = applicationContext.getString(R.string.loadApp)
                    mainButtons.customButton.setState(ButtonState.Loading)
                    download(URL_UDACITY)
                } else if (mainButtons.radioRetrofit.isChecked) {
                    fileName = applicationContext.getString(R.string.retrofit)
                    mainButtons.customButton.setState(ButtonState.Loading)
                    download(URL_RETROFIT)
                } else {
                    Toast.makeText(
                        applicationContext,
                        applicationContext.getString(R.string.selectFile),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
        notificationManager = getSystemService(
            NotificationManager::class.java
        ) as NotificationManager
        setSupportActionBar(binding.toolbar)
        createChannel(
            CHANNEL_ID,
            CHANNEL_NAME
        )
        setContentView(binding.root)
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            if (downloadID == id) {
                notificationManager.sendNotification(downloadStatus, fileName, applicationContext)
                binding.mainButtons.customButton.setState(ButtonState.Completed)
            }

        }
    }

    private fun download(URL: String) {
        val direct = File(getExternalFilesDir(null), "/repos")
        if (!direct.exists()) {
            direct.mkdirs()
        }
        val request =
            DownloadManager.Request(Uri.parse(URL))
                .setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)
                .setDestinationInExternalPublicDir(
                    Environment.DIRECTORY_DOWNLOADS,
                    "/repos/repository.zip"
                )

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID = downloadManager.enqueue(request)

        val query = DownloadManager.Query().setFilterById(downloadID)
        Thread(Runnable {
            var downloading = true
            while (downloading) {
                val cursor: Cursor = downloadManager.query(query)
                cursor.moveToFirst()
                if (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) == DownloadManager.STATUS_SUCCESSFUL) {
                    downloading = false
                }
                val status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                downloadStatus = statusMessage(status)
                cursor.close()
            }
        }).start()
    }


    private fun statusMessage(status: Int): String {
        var msg = ""
        msg = when (status) {
            DownloadManager.STATUS_FAILED -> "Failed"
            DownloadManager.STATUS_PAUSED -> "Paused"
            DownloadManager.STATUS_PENDING -> "Pending"
            DownloadManager.STATUS_RUNNING -> "Downloading"
            DownloadManager.STATUS_SUCCESSFUL -> "Success"
            else -> "There's nothing to download"
        }
        return msg
    }

    private fun createChannel(channelId: String, channelName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            )
                .apply {
                    setShowBadge(false)
                }

            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = "Download File"
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }

    companion object {
        private const val URL_GLIDE =
            "https://github.com/bumptech/glide/archive/refs/heads/master.zip"
        private const val URL_UDACITY =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/refs/heads/master.zip"
        private const val URL_RETROFIT =
            "https://github.com/square/retrofit/archive/refs/heads/master.zip"
        private const val CHANNEL_ID = "download_file_channel"
        private const val CHANNEL_NAME = "download_file"
    }
}