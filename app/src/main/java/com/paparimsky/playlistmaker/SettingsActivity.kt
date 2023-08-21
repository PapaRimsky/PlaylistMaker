package com.paparimsky.playlistmaker

import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.paparimsky.playlistmaker.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.switchTheme.isChecked = App.darkTheme

        binding.switchTheme.isChecked = this.resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
        binding.backToMainFromSettings.setOnClickListener {
            finish()
        }
        binding.toShare.setOnClickListener {
            Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, getString(R.string.view_to_share))
                startActivity(this)
            }
        }
        binding.toSupport.setOnClickListener {
            Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.view_to_support_address)))
                putExtra(Intent.EXTRA_SUBJECT, getString(R.string.view_to_support_subject))
                putExtra(Intent.EXTRA_TEXT, getString(R.string.view_to_support_text))
                startActivity(this)
            }
        }
        binding.toAgreement.setOnClickListener {
            Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(getString(R.string.view_to_agreement))
                startActivity(this)
            }
        }
        binding.switchTheme.setOnClickListener{}
        binding.switchTheme.setOnCheckedChangeListener  { _, checked  ->
            (applicationContext as App).switchTheme(checked)
        }
    }
}