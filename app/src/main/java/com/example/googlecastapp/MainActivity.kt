package com.example.googlecastapp

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.cast.*
import com.google.android.gms.cast.framework.*
import com.google.android.gms.cast.framework.media.RemoteMediaClient

class MainActivity : AppCompatActivity() {

    private lateinit var castContext: CastContext
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val castButton = findViewById<Button>(R.id.cast_button)

        // Initialize Cast SDK
        castContext = CastContext.getSharedInstance(this)
        sessionManager = castContext.sessionManager

        castButton.setOnClickListener {
            castVideo()
        }
    }

    private fun castVideo() {
        val session = sessionManager.currentCastSession
        if (session != null && session.isConnected) {
            val remoteMediaClient = session.remoteMediaClient
            if (remoteMediaClient != null) {
                val mediaInfo = MediaInfo.Builder("https://videolink-test.mycdn.me/?pct=1&sig=6QN0vp0y3BE&ct=0&clientType=45&mid=193241622673&type=5")
                    .setStreamType(MediaInfo.STREAM_TYPE_BUFFERED)
                    .setContentType("video/mp4")
                    .setMetadata(MediaMetadata(MediaMetadata.MEDIA_TYPE_MOVIE).apply {
                        putString(MediaMetadata.KEY_TITLE, "Test Video")
                    })
                    .build()

                val mediaLoadRequestData = MediaLoadRequestData.Builder()
                    .setMediaInfo(mediaInfo)
                    .build()

                remoteMediaClient.load(mediaLoadRequestData).setResultCallback { result ->
                    if (!result.status.isSuccess) {
                        println("Failed to start video on Cast device")
                    }
                }
            }
        } else {
            println("No Cast device available")
        }
    }
}
