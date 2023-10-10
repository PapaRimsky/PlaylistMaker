package com.paparimsky.playlistmaker

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.paparimsky.playlistmaker.ImageViewExtensions.loadImageGlide
import java.text.SimpleDateFormat
import java.util.*

class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val trackImage: ImageView = itemView.findViewById(R.id.track_image)
    private val trackName: TextView = itemView.findViewById(R.id.track_name)
    private val artistName: TextView = itemView.findViewById(R.id.artist_name)
    private val trackTime: TextView = itemView.findViewById(R.id.track_time)

    fun bind(model: Track) {
        if (model.artworkUrl100.isNotEmpty()) {
            trackImage.loadImageGlide(model.artworkUrl100, LAYOUT_RADIUS, R.drawable.note)
        } else {
            trackImage.setImageResource(R.drawable.note)
        }
        trackName.text = model.trackName
        artistName.text = model.artistName
        trackTime.text =
            SimpleDateFormat("mm:ss", Locale.getDefault()).format(model.trackTimeMillis)
    }
    companion object {
        const val LAYOUT_RADIUS = 2f
    }
}