package com.example.loadapp

import android.app.NotificationManager
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.loadapp.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    private lateinit var notificationManager: NotificationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(
            this, R.layout.activity_detail
        )
        notificationManager =
            getSystemService(
                NotificationManager::class.java
            ) as NotificationManager
        notificationManager.cancelNotifications()
        val downloadStatus = intent.getStringExtra("downloadStatus")
        val fileName = intent.getStringExtra("fileName")
        binding.apply {
            if (downloadStatus.equals("Success")) {
                detailButtons.status.setTextColor(applicationContext.getColor(R.color.colorAccent))
            } else {
                detailButtons.status.setTextColor(applicationContext.getColor(R.color.red))
            }

            detailButtons.fileName.text = fileName
            detailButtons.status.text = downloadStatus

            detailButtons.button.setOnClickListener {
                val intent = Intent(this@DetailActivity, MainActivity::class.java)
                startActivity(intent)
            }
        }
        setSupportActionBar(binding.toolbar)
    }

}
