package com.paparimsky.playlistmaker2

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class SearchActivity : AppCompatActivity() {
    companion object {
        const val SEARCH_TEXT = "SEARCH_TEXT"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        val imageButtonBack = findViewById<ImageButton>(R.id.backToMainFromSearch)
        val editText = findViewById<EditText>(R.id.editText)
        val display = findViewById<LinearLayout>(R.id.display)
        val cross = findViewById<ImageView>(R.id.cross)
        editText.requestFocus()
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        if (savedInstanceState != null) {
            editText.setText(savedInstanceState.getString(SEARCH_TEXT,""))
        }
        imageButtonBack.setOnClickListener {
            val displayMain = Intent(this, MainActivity::class.java)
            startActivity(displayMain)
            finish()
        }
        display.setOnClickListener {
            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(editText.windowToken, 0)
            editText.clearFocus()
        }
        cross.setOnClickListener {
            editText.setText("")
        }
        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                //
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                cross.visibility = clearCrossVisibility(s)
            }

            override fun afterTextChanged(s: Editable?) {
                Toast.makeText(this@SearchActivity, "Поиск...", Toast.LENGTH_SHORT).show()
            }
        }
        editText.addTextChangedListener(simpleTextWatcher)
    }
    private fun clearCrossVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) View.GONE else View.VISIBLE
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val editText = findViewById<EditText>(R.id.editText)
        outState.putString(SEARCH_TEXT,editText.text.toString())
    }
}