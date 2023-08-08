package com.paparimsky.playlistmaker

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.paparimsky.playlistmaker.databinding.ActivitySearchBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding

    private companion object {
        const val SEARCH_TEXT = "SEARCH_TEXT"
    }

    private val iTunesAPILink = "https://itunes.apple.com"

    private val retrofit = Retrofit.Builder()
        .baseUrl(iTunesAPILink)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val iTunesService = retrofit.create(ITunesAPI::class.java)

    private val tracks = ArrayList<Track>()

    private val adapter = TrackAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.editText.requestFocus()
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        if (savedInstanceState != null) {
            binding.editText.setText(savedInstanceState.getString(SEARCH_TEXT, ""))
        }
        binding.backToMainFromSearch.setOnClickListener {
            finish()
        }
        binding.display.setOnClickListener {
            clearKeyboard()
        }
        binding.cross.setOnClickListener {
            binding.editText.setText("")
            clearKeyboard()
        }
        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                //
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.cross.visibility = clearCrossVisibility(s)
            }

            override fun afterTextChanged(s: Editable?) {
                //
            }
        }
        binding.editText.addTextChangedListener(simpleTextWatcher)

        val recycler = findViewById<RecyclerView>(R.id.trackList)
        recycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recycler.adapter = adapter

        binding.editText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                getTracks()
            }
            false
        }
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

    private fun getTracks(){
        if (binding.editText.text.isNotEmpty()) {
            iTunesService.getTracks(binding.editText.text.toString()).enqueue(object : Callback<TrackResponse> {
                @SuppressLint("NotifyDataSetChanged")
                override fun onResponse(
                    call: Call<TrackResponse>,
                    response: Response<TrackResponse>
                ) {
                    if (response.code() == 200) {
                        tracks.clear()
                        if (response.body()?.results?.isNotEmpty() == true) {
                            tracks.addAll(response.body()?.results!!)
                            adapter.notifyDataSetChanged()
                        }
                        if (tracks.isEmpty()) {
                            showMessage(getString(R.string.nothing_found), "","NF")
                        }
                    } else {
                        showMessage(getString(R.string.errors_with_link), response.code().toString(),"E")
                    }
                }
                override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
                    showMessage(getString(R.string.errors_with_link), t.message.toString(),"E")
                }
            })
        }
    }
    @SuppressLint("NotifyDataSetChanged")
    private fun showMessage(text: String, additionalMessage: String, typeMessage: String) {
        if (text.isNotEmpty()) {
            binding.searchError.visibility = View.VISIBLE
            tracks.clear()
            adapter.notifyDataSetChanged()
            binding.textError.text = text
            when(typeMessage){
                "NF"->binding.imageError.setImageResource(R.drawable.error_nothing_found)
                "E"-> {
                    binding.imageError.setImageResource(R.drawable.error)
                    binding.buttonError.visibility = View.VISIBLE
                }
            }
            if (additionalMessage.isNotEmpty()) {
                Toast.makeText(applicationContext, additionalMessage, Toast.LENGTH_LONG)
                    .show()
            }
        } else {
            binding.searchError.visibility = View.GONE
            binding.buttonError.visibility = View.GONE
        }
    }
    private fun show(){
        Toast.makeText(applicationContext, "ААА", Toast.LENGTH_LONG)
            .show()
    }
}