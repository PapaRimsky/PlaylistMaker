package com.paparimsky.playlistmaker2

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity: AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        val buttonSearch = findViewById<Button>(R.id.backToMain)
        buttonSearch.setOnClickListener{
            val displayMain = Intent(this, MainActivity::class.java)
            startActivity(displayMain)
            finish()
        }
    }
}