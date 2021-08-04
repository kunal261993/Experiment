package com.example.loadapp

import android.Manifest
import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.RadioGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.databinding.DataBindingUtil
import com.example.loadapp.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar
import java.io.File


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private var downloadID: Long = 0

    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action
    //private lateinit var layout: View
    private lateinit var radioGroup: RadioGroup
    var msg: String? = ""
    var lastMsg = ""

    private var permissionGranted = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
        notificationManager = getSystemService(
            NotificationManager::class.java
        ) as NotificationManager
        setSupportActionBar(binding.toolbar)

        binding.button123.setOnClickListener{
            var selectedId = binding.radiaGroup.checkedRadioButtonId
            Log.i("ABCD",selectedId.toString())
            if(binding.radiaId1.isChecked)
            {
                Log.i("A",selectedId.toString())
                download(URL1)
            }
            else if(binding.radiaId2.isChecked)
            {
                Log.i("B",selectedId.toString())
                download(URL2)
            }
            else if(binding.radiaId3.isChecked)
            {
                Log.i("C",selectedId.toString())
                download(URL3)
            }
            else
            {
                Toast.makeText(this, "Please select file to download", Toast.LENGTH_SHORT).show()
            }

        }
        createChannel(
            "A",
            "ABCD"
        )
        setContentView(binding.root)
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            Log.i("ABCD: ", id.toString())
            if (downloadID == id) {
                notificationManager.sendNotification(lastMsg,applicationContext)
            }

        }
    }

    private fun download(URL:String) {
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
               /* .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)*/
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,"/repos/reopsitory.zip" )

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID = downloadManager.enqueue(request)// enqueue puts the download request in the queue.

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
                msg = statusMessage(status)
                if (msg != lastMsg) {
                    this.runOnUiThread {
                        //Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
                        binding.button123.text=msg
                    }
                    lastMsg = msg ?: ""
                }
                cursor.close()
            }
        }).start()

    }


    private fun statusMessage(status: Int): String? {
        var msg = ""
        msg = when (status) {
            DownloadManager.STATUS_FAILED -> "Failed"
            DownloadManager.STATUS_PAUSED -> "Paused"
            DownloadManager.STATUS_PENDING -> "Pending"
            DownloadManager.STATUS_RUNNING -> "Downloading..."
            DownloadManager.STATUS_SUCCESSFUL -> "Success"
            else -> "There's nothing to download"
        }
        return msg
    }

    private fun createChannel(channelId: String, channelName: String) {
        // TODO: Step 1.6 START create a channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                // TODO: Step 2.4 change importance
                NotificationManager.IMPORTANCE_HIGH
            )
                // TODO: Step 2.6 disable badges for this channel
                .apply {
                    setShowBadge(false)
                }

            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = "Time for breakfast"
            notificationManager.createNotificationChannel(notificationChannel)


            // TODO: Step 1.6 END create channel
        }
    }
   /* override fun onDestroy() {
        super.onDestroy()
        // using broadcast method
        unregisterReceiver(receiver)
    }*/

    companion object {
        private const val URL1 =
            "https://github.com/bumptech/glide/archive/refs/heads/master.zip"
        private const val URL2 =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/refs/heads/master.zip"
        private const val URL3 =
            "https://github.com/square/retrofit/archive/refs/heads/master.zip"
        private const val CHANNEL_ID = "channelId"
    }
    /*private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Log.i("Permission: ", "Granted")
            } else {
                Log.i("Permission: ", "Denied")
            }
        }*/

/*    fun onClickRequestPermission(view: View) {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED -> {
                *//*layout.showSnackbar(
                    view,
                    "Permission Granted",
                    Snackbar.LENGTH_LONG,
                    null
                ) {}*//*
                permissionGranted = true
            }

            ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) -> {
                layout.showSnackbar(
                    view,
                    "Permission Required",
                    Snackbar.LENGTH_INDEFINITE,
                    "OK"
                ) {
                    requestPermissionLauncher.launch(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                }
                permissionGranted = false
            }

            else -> {
                requestPermissionLauncher.launch(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                permissionGranted = false
            }
        }
    }*/
}

/*
fun View.showSnackbar(
    view: View,
    msg: String,
    length: Int,
    actionMessage: CharSequence?,
    action: (View) -> Unit
) {
    val snackbar = Snackbar.make(view, msg, length)
    if (actionMessage != null) {
        snackbar.setAction(actionMessage) {
            action(this)
        }.show()
    } else {
        snackbar.show()
    }
}

*/
