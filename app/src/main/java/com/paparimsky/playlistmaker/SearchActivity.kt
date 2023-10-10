package com.paparimsky.playlistmaker

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.paparimsky.playlistmaker.databinding.ActivitySearchBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding

    private val retrofit = Retrofit.Builder()
        .baseUrl(ITUNES_API_LINK)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val iTunesService = retrofit.create(ITunesAPI::class.java)

    private val adapter = TrackAdapter {
        addTrackToMemoryAndGoToPlayer(it)
    }
    private val searchHistory = SearchHistory(App.sharedPrefs)

    private val searchedAdapter = SearchedTrackAdapter {
        addTrackToMemoryAndGoToPlayer(it)
    }

    private var listener: SharedPreferences.OnSharedPreferenceChangeListener? = null

    private var isClickAllowed = true

    private val handler = Handler(Looper.getMainLooper())

    private val searchRunnable = Runnable { getTracks() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.backToMainFromSearch.setOnClickListener {
            finish()
        }
        binding.editText.requestFocus()
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        if (savedInstanceState != null) {
            binding.editText.setText(savedInstanceState.getString(SEARCH_TEXT, ""))
        }
        binding.root.setOnClickListener {
            binding.editText.clearFocus()
            clearKeyboard()
        }
        loadTracks()
        if (searchedAdapter.tracksSearched.isNotEmpty()) {
            displayStatus(binding.searchBefore, DisplayStatus.VISIBLE)
        } else displayStatus(binding.searchBefore, DisplayStatus.GONE)
        binding.cross.setOnClickListener {
            binding.editText.setText("")
            if (adapter.tracks.isNotEmpty()) {
                setUpdatedTracks(emptyList())
            }
            binding.searchError.visibility = View.GONE
            binding.buttonError.visibility = View.GONE
            if (searchedAdapter.tracksSearched.isNotEmpty()) {
                displayStatus(binding.searchBefore, DisplayStatus.VISIBLE)
            } else displayStatus(binding.searchBefore, DisplayStatus.GONE)
            clearKeyboard()
        }
        binding.trackList.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.trackList.adapter = adapter
        binding.tracksSearched.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.tracksSearched.adapter = searchedAdapter
        binding.editText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                getTracks()
            }
            false
        }
        binding.buttonError.setOnClickListener {
            getTracks()
        }
        binding.editText.setOnFocusChangeListener { _, hasFocus ->
            if (searchedAdapter.tracksSearched.isNotEmpty() && hasFocus && binding.editText.text.isEmpty()) {
                displayStatus(binding.searchBefore, DisplayStatus.VISIBLE)
            } else displayStatus(binding.searchBefore, DisplayStatus.GONE)
        }
        binding.editText.doOnTextChanged { s, _, _, _ ->
            searchDebounce()
            binding.cross.visibility = clearCrossVisibility(s)
            if (searchedAdapter.tracksSearched.isNotEmpty() && binding.editText.hasFocus() && s?.isEmpty() == true) {
                displayStatus(binding.searchBefore, DisplayStatus.VISIBLE)
                displayStatus(binding.trackList, DisplayStatus.GONE)
            } else {
                displayStatus(binding.searchBefore, DisplayStatus.GONE)
                displayStatus(binding.trackList, DisplayStatus.VISIBLE)
            }
        }
        binding.clearHistory.setOnClickListener {
            App.sharedPrefs
                ?.edit()
                ?.remove(App.SEARCHED_TRACKS_KEY)
                ?.apply()
            displayStatus(binding.searchBefore, DisplayStatus.GONE)
        }
        listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key == App.SEARCHED_TRACKS_KEY) {
                loadTracks()
            }
        }

        App.sharedPrefs?.registerOnSharedPreferenceChangeListener(listener)
    }

    override fun onPause() {
        super.onPause()
        App.sharedPrefs?.unregisterOnSharedPreferenceChangeListener(listener)
    }

    private fun clearCrossVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) View.GONE else View.VISIBLE
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_TEXT, binding.editText.text.toString())
    }

    private fun clearKeyboard() {
        val inputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(binding.editText.windowToken, 0)
        binding.editText.clearFocus()
    }

    private fun getTracks() {
        if (binding.editText.text.isNotEmpty()) {
            displayStatus(binding.trackList, DisplayStatus.GONE)
            displayStatus(binding.progressBar, DisplayStatus.VISIBLE)
            iTunesService.getTracks(binding.editText.text.toString())
                .enqueue(object : Callback<TrackResponse> {
                    override fun onResponse(
                        call: Call<TrackResponse>,
                        response: Response<TrackResponse>
                    ) {
                        displayStatus(binding.progressBar, DisplayStatus.GONE)
                        if (response.code() == 200) {
                            if (response.body()?.results?.isNotEmpty() == true) {
                                displayStatus(binding.trackList, DisplayStatus.VISIBLE)
                                displayStatus(binding.searchError, DisplayStatus.GONE)
                                displayStatus(binding.buttonError, DisplayStatus.GONE)
                                setUpdatedTracks(response.body()?.results!!)
                            } else {
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
                        displayStatus(binding.progressBar, DisplayStatus.GONE)
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
            displayStatus(binding.searchError, DisplayStatus.VISIBLE)
            setUpdatedTracks(emptyList())
            binding.textError.text = text
            when (typeMessage) {
                MessageType.NF -> binding.imageError.setImageResource(R.drawable.error_nothing_found)
                MessageType.E -> {
                    binding.imageError.setImageResource(R.drawable.error)
                    displayStatus(binding.buttonError, DisplayStatus.VISIBLE)
                }
            }
            if (additionalMessage.isNotEmpty()) {
                Toast.makeText(applicationContext, additionalMessage, Toast.LENGTH_LONG)
                    .show()
            }
        } else {
            displayStatus(binding.searchError, DisplayStatus.GONE)
            displayStatus(binding.buttonError, DisplayStatus.GONE)
        }
    }

    private fun setUpdatedTracks(updatedTracks: List<Track>) {
        val diffResult =
            DiffUtil.calculateDiff(TrackDiffUtilCallback(adapter.tracks, updatedTracks))
        adapter.tracks.clear()
        adapter.tracks.addAll(updatedTracks)
        diffResult.dispatchUpdatesTo(adapter)
    }

    private fun addTrackToMemoryAndGoToPlayer(track: Track) {
        if (clickOnTrackDebounce()) {
            searchHistory.saveSearchedTrack(track)
            val intent = Intent(this, AudioPlayerActivity::class.java)
            intent.putExtra("track", Gson().toJson(track))
            startActivity(intent)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun loadTracks() {
        searchedAdapter.tracksSearched = searchHistory.getHistory()
        searchedAdapter.notifyDataSetChanged()
    }

    private fun displayStatus(view: View, status: DisplayStatus) {
        view.isVisible = status != DisplayStatus.GONE
    }

    private fun clickOnTrackDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    private fun searchDebounce() {
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }
    private companion object {
        const val SEARCH_TEXT = "SEARCH_TEXT"
        const val ITUNES_API_LINK = "https://itunes.apple.com"
        private const val CLICK_DEBOUNCE_DELAY = 1000L
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }
}