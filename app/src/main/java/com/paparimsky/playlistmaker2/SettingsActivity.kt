package com.paparimsky.playlistmaker2

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity: AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        val imageButtonBack = findViewById<ImageButton>(R.id.backToMainFromSettings)
        val viewToShare = findViewById<LinearLayout>(R.id.toShare)
        val viewToSupport = findViewById<LinearLayout>(R.id.toSupport)
        val viewToAgreement = findViewById<LinearLayout>(R.id.toAgreement)
        val linearTheme = findViewById<LinearLayout>(R.id.changeTheme)
        val switchTheme = findViewById<Switch>(R.id.switchTheme)
        imageButtonBack.setOnClickListener{
            val displayMain = Intent(this, MainActivity::class.java)
            startActivity(displayMain)
            finish()
        }
        viewToShare.setOnClickListener{
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_TEXT, "https://practicum.yandex.ru/android-developer")
            startActivity(shareIntent)
        }
        viewToSupport.setOnClickListener{
            val supIntent = Intent(Intent.ACTION_SENDTO)
            supIntent.data = Uri.parse("mailto:")
            supIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf("khvishuk@yandex.ru"))
            supIntent.putExtra(Intent.EXTRA_SUBJECT, "Сообщение разработчикам и разработчицам приложения Playlist Maker")
            supIntent.putExtra(Intent.EXTRA_TEXT, "Спасибо разработчикам и разработчицам за крутое приложение!")
            startActivity(supIntent)
        }
        viewToAgreement.setOnClickListener{
            val agreeIntent = Intent(Intent.ACTION_VIEW)
            agreeIntent.data = Uri.parse("https://yandex.ru/legal/practicum_offer/")
            startActivity(agreeIntent)
        }
        linearTheme.setOnClickListener{
            switchTheme.isChecked= !switchTheme.isChecked
        }
    }
}