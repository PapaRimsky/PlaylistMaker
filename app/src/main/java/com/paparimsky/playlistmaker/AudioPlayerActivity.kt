package com.paparimsky.playlistmaker

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.google.gson.Gson
import com.paparimsky.playlistmaker.ImageViewExtensions.loadImageGlide
import com.paparimsky.playlistmaker.databinding.ActivityAudioPlayerBinding
import java.text.SimpleDateFormat
import java.util.*

class AudioPlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAudioPlayerBinding

    private var playerState = STATE_DEFAULT

    private var mediaPlayer = MediaPlayer()

    private val handler = Handler(Looper.getMainLooper())

    private val audioRunnable: Runnable = updateAudioTimer()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAudioPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val jsonTrack = intent.getStringExtra("track")
        val track = Gson().fromJson(jsonTrack, Track::class.java)
        preparePlayer(track.previewUrl)
        binding.play.setOnClickListener {
            handler.post(audioRunnable)
            playbackControl()
        }
        binding.backToMainFromPlayer.setOnClickListener {
            finish()
        }
        if (track.artworkUrl100.isNotEmpty()) {
            binding.poster.loadImageGlide(
                track.getCoverArtwork(),
                LAYOUT_RADIUS,
                R.drawable.default_poster
            )
        } else {
            binding.poster.setImageResource(R.drawable.default_poster)
        }
        binding.trackTitle.text = track.trackName
        binding.trackArtist.text = track.artistName
        binding.timeParameter.text =
            SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis)
        if (track.collectionName.isNotEmpty()) {
            binding.albumParameter.isVisible = true
            binding.albumParameter.text = track.collectionName
        } else {
            binding.albumParameter.isVisible = false
        }
        binding.yearParameter.text = track.getYear()
        binding.genreParameter.text = track.primaryGenreName
        binding.countryParameter.text = track.country
    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
        handler.removeCallbacks(audioRunnable)
    }

    private fun preparePlayer(url: String) {
        mediaPlayer.setDataSource(url)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            binding.play.isEnabled = true
            playerState = STATE_PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            binding.play.setImageResource(R.drawable.play)
            playerState = STATE_PREPARED
            handler.removeCallbacks(audioRunnable)
            binding.listenedByTheTime.text = getString(R.string.zero)
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        binding.play.setImageResource(R.drawable.pause)
        playerState = STATE_PLAYING
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        binding.play.setImageResource(R.drawable.play)
        playerState = STATE_PAUSED
        handler.removeCallbacks(audioRunnable)
    }

    private fun playbackControl() {
        when (playerState) {
            STATE_PLAYING -> {
                pausePlayer()
            }
            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer()
            }
        }
    }

    private fun updateAudioTimer(): Runnable {
        return object : Runnable {
            override fun run() {
                if (playerState == STATE_PLAYING) {
                    binding.listenedByTheTime.text = SimpleDateFormat(
                        "mm:ss",
                        Locale.getDefault()
                    ).format(mediaPlayer.currentPosition)
                    handler.postDelayed(this, DELAY)
                }
            }
        }
    }
    companion object {
        const val LAYOUT_RADIUS = 8f
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
        private const val DELAY = 300L
    }
}