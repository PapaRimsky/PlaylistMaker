package com.paparimsky.playlistmaker2

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.paparimsky.playlistmaker2.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.search.setOnClickListener {
            val displaySettings = Intent(this, SearchActivity::class.java)
            startActivity(displaySettings)
        }
        binding.media.setOnClickListener {
            val displaySettings = Intent(this, MediaActivity::class.java)
            startActivity(displaySettings)
        }
        binding.settings.setOnClickListener {
            val displaySettings = Intent(this, SettingsActivity::class.java)
            startActivity(displaySettings)
        }
    }
}