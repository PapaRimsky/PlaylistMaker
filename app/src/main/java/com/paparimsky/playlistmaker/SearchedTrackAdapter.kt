package com.paparimsky.playlistmaker

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class SearchedTrackAdapter(private val clickListener: TrackClickListener) :
    RecyclerView.Adapter<TrackViewHolder>() {

    var tracksSearched = ArrayList<Track>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.track_view, parent, false)
        return TrackViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(tracksSearched[position])
        holder.itemView.setOnClickListener { clickListener.onTrackClick(tracksSearched[position]) }
    }

    override fun getItemCount() = tracksSearched.size

    fun interface TrackClickListener {
        fun onTrackClick(track: Track)
    }

}