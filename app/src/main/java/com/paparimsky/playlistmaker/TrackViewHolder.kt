package com.paparimsky.playlistmaker

import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import java.text.SimpleDateFormat
import java.util.*

class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val trackImage: ImageView = itemView.findViewById(R.id.track_image)
    private val trackName: TextView = itemView.findViewById(R.id.track_name)
    private val artistName: TextView = itemView.findViewById(R.id.artist_name)
    private val trackTime: TextView = itemView.findViewById(R.id.track_time)

    companion object {
        const val LAYOUT_RADIUS = 2f
    }

    fun bind(model: Track) {
        if (model.artworkUrl100.isNotEmpty()) {
            trackImage.loadImageGlide(model.artworkUrl100, LAYOUT_RADIUS)
        } else {
            trackImage.setImageResource(R.drawable.note)
        }
        trackName.text = model.trackName
        artistName.text = model.artistName
        trackTime.text =
            SimpleDateFormat("mm:ss", Locale.getDefault()).format(model.trackTimeMillis)
    }
}

fun ImageView.loadImageGlide(
    link: String,
    layout_radius: Float? = null,
) {
    Glide.with(context)
        .load(link)
        .placeholder(R.drawable.note)
        .centerCrop()
        .transform(
            RoundedCorners(
                TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    layout_radius ?: 0f,
                    context.resources.displayMetrics
                ).toInt()
            )
        )
        .into(this)

}