package com.example.googlecastapp

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.cast.*
import com.google.android.gms.cast.framework.*
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {

    private lateinit var castContext: CastContext
    private var sessionManager: SessionManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val castButton = findViewById<Button>(R.id.cast_button)

        // Initialize Cast SDK with error handling
        try {
            val executor = Executors.newSingleThreadExecutor()
            CastContext.getSharedInstance(this, executor)
                .addOnSuccessListener { context ->
                    castContext = context
                    sessionManager = castContext.sessionManager // Now initialized safely
                    println("CastContext initialized!")
                }
                .addOnFailureListener {
                    println("Google Cast SDK failed to load!")
                }
        } catch (e: Exception) {
            e.printStackTrace()
            println("Error initializing CastContext: ${e.message}")
        }

        // Handle button click safely
        castButton.setOnClickListener {
            println("Cast button clicked!")
            if (sessionManager != null) {
                castVideo()  // Only call if sessionManager is initialized
            } else {
                println("sessionManager is not initialized yet!")
            }
        }
    }

    private fun castVideo() {
        val session = sessionManager?.currentCastSession
        if (session != null && session.isConnected) {
            println("Google Cast device detected: ${session.castDevice?.friendlyName}")

            val remoteMediaClient = session.remoteMediaClient
            if (remoteMediaClient != null) {
                println("Sending video to ${session.castDevice?.friendlyName}...")

                val mediaInfo =
                    MediaInfo.Builder("https://videolink-test.mycdn.me/?pct=1&sig=6QN0vp0y3BE&ct=0&clientType=45&mid=193241622673&type=5")
                        .setStreamType(MediaInfo.STREAM_TYPE_BUFFERED)
                        .setContentType("video/mp4")
                        .setMetadata(MediaMetadata(MediaMetadata.MEDIA_TYPE_MOVIE).apply {
                            putString(MediaMetadata.KEY_TITLE, "Test Video")
                        })
                        .build()

                val mediaLoadRequestData = MediaLoadRequestData.Builder()
                    .setMediaInfo(mediaInfo)
                    .build()

                remoteMediaClient.load(mediaLoadRequestData)
                    .setResultCallback { result ->
                        if (result.status.isSuccess) {
                            println("Video successfully started on ${session.castDevice?.friendlyName}!")
                        } else {
                            println("Failed to start video on ${session.castDevice?.friendlyName}.")
                        }
                    }
            }
        } else {
            println("No Google Cast device detected. Make sure it's available in Google Home.")
        }
    }
}