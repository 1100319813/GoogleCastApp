package com.example.googlecastapp

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.cast.framework.CastContext
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {

    private lateinit var castContext: CastContext

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main) // ✅ Ensure your XML layout is loaded

        val castButton = findViewById<Button>(R.id.cast_button)

        // ✅ Use the new recommended way to initialize CastContext
        try {
            val executor = Executors.newSingleThreadExecutor()
            CastContext.getSharedInstance(this, executor)
                .addOnSuccessListener { context ->
                    castContext = context
                }
                .addOnFailureListener {
                    println("Google Cast SDK failed to load!")
                }
        } catch (e: Exception) {
            e.printStackTrace()
            println("Error initializing CastContext: ${e.message}")
        }

        castButton.setOnClickListener {
            println("Cast button clicked!")  // ✅ Log output to check button click
        }
    }
}
