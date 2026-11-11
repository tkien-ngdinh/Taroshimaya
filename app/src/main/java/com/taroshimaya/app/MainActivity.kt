package com.taroshimaya.app

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

/**
 * Single Activity entry point hosting Compose UI and AppCompat per-app locale sync (C19).
 */
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val appContainer = (application as TaroshimayaApplication).container
        appContainer.languageManager.syncOnLaunch()
        setContent {
            TaroshimayaApp(appContainer = appContainer)
        }
    }
}
