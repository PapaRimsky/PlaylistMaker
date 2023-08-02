package com.paparimsky.playlistmaker2

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.paparimsky.playlistmaker2.databinding.ActivitySearchBinding

class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding
    private companion object {
        const val SEARCH_TEXT = "SEARCH_TEXT"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.editText.requestFocus()
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        if (savedInstanceState != null) {
            binding.editText.setText(savedInstanceState.getString(SEARCH_TEXT,""))
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
                Toast.makeText(this@SearchActivity, getString(R.string.search), Toast.LENGTH_SHORT).show()
            }
        }
        binding.editText.addTextChangedListener(simpleTextWatcher)
    }
    private fun clearCrossVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) View.GONE else View.VISIBLE
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_TEXT,binding.editText.text.toString())
    }
    private fun clearKeyboard() {
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(binding.editText.windowToken, 0)
        binding.editText.clearFocus()
    }
}