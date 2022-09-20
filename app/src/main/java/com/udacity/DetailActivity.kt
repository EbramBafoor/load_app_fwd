package com.udacity

import android.app.DownloadManager
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.content_detail.*

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)


        if (intent.hasExtra("title")) {
            fileName.text = intent.getStringExtra("title")
            fileName.setTextColor(Color.BLACK)
        }
        if (intent.hasExtra("status")) {
            if (intent?.getIntExtra("status", -1) == DownloadManager.STATUS_SUCCESSFUL) {
                status.text = "Success"
                status.setTextColor(Color.GREEN)
            } else {
                status.text = "Failed"
                status.setTextColor(Color.RED)
            }
        }
    }

}
