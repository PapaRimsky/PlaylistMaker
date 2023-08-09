package com.paparimsky.playlistmaker

import androidx.recyclerview.widget.DiffUtil

class TrackDiffUtilCallback(private val oldList: List<Track>, private val newList: List<Track>) :
    DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].trackId == newList[newItemPosition].trackId
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return when {
            oldList[oldItemPosition].trackName == newList[newItemPosition].trackName -> true
            oldList[oldItemPosition].artistName == newList[newItemPosition].artistName -> true
            oldList[oldItemPosition].trackTimeMillis == newList[newItemPosition].trackTimeMillis -> true
            oldList[oldItemPosition].artworkUrl100 == newList[newItemPosition].artworkUrl100 -> true
            else -> false
        }
    }
}