package com.paparimsky.playlistmaker

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.paparimsky.playlistmaker.databinding.ActivityAudioPlayerBinding
import java.text.SimpleDateFormat
import java.util.*

class AudioPlayerActivity: AppCompatActivity() {

    private lateinit var binding: ActivityAudioPlayerBinding

    companion object {
        const val LAYOUT_RADIUS = 8f
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAudioPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val jsonTrack = intent.getStringExtra("track")
        val track = Gson().fromJson(jsonTrack, Track::class.java)
        binding.backToMainFromPlayer.setOnClickListener {
            finish()
        }
        if (track.artworkUrl100.isNotEmpty()) {
            binding.poster.loadImageGlide(track.getCoverArtwork(), LAYOUT_RADIUS, R.drawable.default_poster)
        } else {
            binding.poster.setImageResource(R.drawable.default_poster)
        }
        binding.trackTitle.text = track.trackName
        binding.trackArtist.text = track.artistName
        binding.timeParameter.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis)
        if (track.collectionName.isNotEmpty()){
            binding.albumParameter.visibility = View.VISIBLE
            binding.albumParameter.text = track.collectionName
        } else {
            binding.albumParameter.visibility = View.GONE
        }
        binding.yearParameter.text = track.getYear()
        binding.genreParameter.text = track.primaryGenreName
        binding.countryParameter.text = track.country
    }
}