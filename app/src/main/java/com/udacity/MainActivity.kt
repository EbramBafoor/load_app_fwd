package com.udacity

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.DOWNLOAD_SERVICE
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings.Global.getString
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.getSystemService
import com.udacity.Constant.CHANNEL_ID
import com.udacity.Constant.DOWNLOAD_ID
import com.udacity.Constant.GLIDE_URL
import com.udacity.Constant.NOTIFICATION_ID
import com.udacity.Constant.REQUEST_CODE
import com.udacity.Constant.RETROFIT_URL
import com.udacity.Constant.UDACITY_URL
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity() {

    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
        createChannel()

        customBtn.setOnClickListener {
            choseWhatToDownload()
        }

    }

    private val receiver = object : BroadcastReceiver() {
        @SuppressLint("Range")
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            if (DOWNLOAD_ID == id) {
                val query = DownloadManager.Query()
                val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
                val cursor = downloadManager.query(query)
                if (cursor.moveToFirst()) {
                    val success =
                        cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                    val downloadTitle =
                        cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_TITLE))
                    notificationManager.createNotification(
                        applicationContext,
                        downloadTitle,
                        success
                    )
                    customBtn.downloadCompleted()
                }
            }
        }
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private fun NotificationManager.createNotification(
        applicationContext: Context, title: String, status: Int

    ) {
        val contentIntent = Intent(applicationContext, DetailActivity::class.java)
        pendingIntent = PendingIntent.getActivity(
            applicationContext, NOTIFICATION_ID, contentIntent, PendingIntent.FLAG_UPDATE_CURRENT
        )
        contentIntent.putExtra("title", title)
        contentIntent.putExtra("status", status)
        val downloadedPendingIntent = PendingIntent.getActivity(
            applicationContext,
            REQUEST_CODE,
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        action = NotificationCompat.Action(
            R.drawable.ic_assistant_black_24dp,
            applicationContext.getString(R.string.notification_title),
            downloadedPendingIntent
        )
        val builder = NotificationCompat.Builder(
            applicationContext,
            CHANNEL_ID
        ).setSmallIcon(R.drawable.ic_assistant_black_24dp)
            .setContentTitle(applicationContext.getString(R.string.notification_title))
            .setContentText(applicationContext.getString(R.string.notification_description))
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .addAction(
                action
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
        notify(NOTIFICATION_ID, builder.build())
    }

    private fun createChannel(
        channelId: String = CHANNEL_ID,
        channelName: String = "download channel"
    ) {
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
            notificationChannel.description = getString(R.string.notification_description)
            notificationManager = this.getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    private fun download(
        title: String,
        description: String,
        url: String
    ) {
        customBtn.startDownload()
        val request =
            DownloadManager.Request(Uri.parse(url))
                .setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        DOWNLOAD_ID =
            downloadManager.enqueue(request)// enqueue puts the download request in the queue.
    }

    private fun choseWhatToDownload() {
        when(radioGroup.checkedRadioButtonId){
            R.id.glideRadioBtn -> {
                download(
                    "Glide",
                    getString(R.string.glide_download),
                    GLIDE_URL
                )
            }
            R.id.udacityRadioBtn -> {
                download(
                    "Udacity",
                    getString(R.string.load_app_download),
                    UDACITY_URL
                )
            }
            R.id.retrofitRadioBtn -> {
                download(
                    "Retrofit",
                    getString(R.string.retrofit_download),
                    RETROFIT_URL
                )
            }
            else -> {
                Toast.makeText(
                    this,
                    "Please Select one of the Files",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}









