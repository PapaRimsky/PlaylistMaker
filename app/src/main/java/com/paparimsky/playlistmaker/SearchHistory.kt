package com.paparimsky.playlistmaker

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SearchHistory(private val sharedPrefs: SharedPreferences?) {

    private val maxHistorySize = 10

    fun getHistory(): ArrayList<Track> {
        val type = object : TypeToken<ArrayList<Track>>() {}.type
        val jsonString = sharedPrefs?.getString(App.SEARCHED_TRACKS_KEY, null) ?: return ArrayList()
        return Gson().fromJson(jsonString, type)
    }

    fun saveSearchedTrack(track: Track) {
        val searchedTracks: ArrayList<Track> = getHistory()
        val tracks = ArrayList<Track>()
        if (searchedTracks.isNotEmpty()) {
            for (i in searchedTracks) {
                if (i.trackId != track.trackId) {
                    tracks.add(i)
                }
            }
        }
        tracks.add(0, track)
        if (tracks.size > maxHistorySize) {
            tracks.removeLast()
        }
        sharedPrefs?.edit()
            ?.putString(App.SEARCHED_TRACKS_KEY, Gson().toJson(tracks))
            ?.apply()
    }
}