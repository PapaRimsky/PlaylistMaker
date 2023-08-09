package com.paparimsky.playlistmaker

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.paparimsky.playlistmaker.databinding.ActivitySearchBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : AppCompatActivity() {

    private var binding: ActivitySearchBinding? = null

    private companion object {
        const val SEARCH_TEXT = "SEARCH_TEXT"
        const val ITUNES_API_LINK = "https://itunes.apple.com"
    }

    private val retrofit = Retrofit.Builder()
        .baseUrl(ITUNES_API_LINK)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val iTunesService = retrofit.create(ITunesAPI::class.java)

    private val tracks = ArrayList<Track>()

    private val adapter = TrackAdapter()

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        binding?.editText?.requestFocus()
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        if (savedInstanceState != null) {
            binding?.editText?.setText(savedInstanceState.getString(SEARCH_TEXT, ""))
        }
        binding?.backToMainFromSearch?.setOnClickListener {
            finish()
        }
        binding?.display?.setOnClickListener {
            clearKeyboard()
        }
        binding?.cross?.setOnClickListener {
            binding?.editText?.setText("")
            if (tracks.isNotEmpty()) {
                tracks.clear()
                adapter.notifyDataSetChanged()
            }
            binding?.searchError?.visibility = View.GONE
            binding?.buttonError?.visibility = View.GONE

            clearKeyboard()
        }

        binding?.editText?.doOnTextChanged { s, _, _, _ ->
            binding?.cross?.visibility = clearCrossVisibility(s)
        }

        binding?.trackList?.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding?.trackList?.adapter = adapter
        adapter.tracks = tracks
        binding?.editText?.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                getTracks()
            }
            false
        }
        binding?.buttonError?.setOnClickListener {
            getTracks()
        }
    }

    private fun clearCrossVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) View.GONE else View.VISIBLE
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_TEXT, binding?.editText?.text.toString())
    }

    private fun clearKeyboard() {
        val inputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(binding?.editText?.windowToken, 0)
        binding?.editText?.clearFocus()
    }

    private fun getTracks() {
        if (binding?.editText?.text?.isNotEmpty() == true) {
            iTunesService.getTracks(binding?.editText?.text.toString())
                .enqueue(object : Callback<TrackResponse> {
                    override fun onResponse(
                        call: Call<TrackResponse>,
                        response: Response<TrackResponse>
                    ) {
                        if (response.code() == 200) {
                            if (response.body()?.results?.isNotEmpty() == true) {
                                setUpdatedTracks(response.body()?.results!!)
                            }
                            if (tracks.isEmpty()) {
                                showMessage(getString(R.string.nothing_found), "", MessageType.NF)
                            }
                        } else {
                            showMessage(
                                getString(R.string.errors_with_link),
                                response.code().toString(),
                                MessageType.E
                            )
                        }
                    }

                    override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
                        showMessage(
                            getString(R.string.errors_with_link),
                            t.message.toString(),
                            MessageType.E
                        )
                    }
                })
        }
    }

    private fun showMessage(text: String, additionalMessage: String, typeMessage: MessageType) {
        if (text.isNotEmpty()) {
            binding?.searchError?.visibility = View.VISIBLE
            setUpdatedTracks(emptyList())
            binding?.textError?.text = text
            when (typeMessage) {
                MessageType.NF -> binding?.imageError?.setImageResource(R.drawable.error_nothing_found)
                MessageType.E -> {
                    binding?.imageError?.setImageResource(R.drawable.error)
                    binding?.buttonError?.visibility = View.VISIBLE
                }
            }
            if (additionalMessage.isNotEmpty()) {
                Toast.makeText(applicationContext, additionalMessage, Toast.LENGTH_LONG)
                    .show()
            }
        } else {
            binding?.searchError?.visibility = View.GONE
            binding?.buttonError?.visibility = View.GONE
        }
    }

    private fun setUpdatedTracks(updatedTracks: List<Track>) {
        val diffResult =
            DiffUtil.calculateDiff(TrackDiffUtilCallback(adapter.tracks, updatedTracks))
        adapter.tracks.clear()
        adapter.tracks.addAll(updatedTracks)
        diffResult.dispatchUpdatesTo(adapter)
    }
}