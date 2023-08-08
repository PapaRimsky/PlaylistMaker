package com.paparimsky.playlistmaker

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ITunesAPI {
    @GET("/search?entity=song")
    fun getTracks(@Query("term") text: String) : Call<TrackResponse>
}