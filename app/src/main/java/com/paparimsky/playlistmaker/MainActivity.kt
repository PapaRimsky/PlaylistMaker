package com.paparimsky.playlistmaker

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val buttonSearch = findViewById<Button>(R.id.search)
        val buttonMedia = findViewById<Button>(R.id.media)
        val buttonSettings = findViewById<Button>(R.id.settings)

        buttonSearch.setOnClickListener {
            val displaySettings = Intent(this, SearchActivity::class.java)
            startActivity(displaySettings)
        }
        buttonMedia.setOnClickListener {
            val displaySettings = Intent(this, MediaActivity::class.java)
            startActivity(displaySettings)
        }
        buttonSettings.setOnClickListener {
            val displaySettings = Intent(this, SettingsActivity::class.java)
            startActivity(displaySettings)
        }
    }
}