package com.paparimsky.playlistmaker

import android.app.Application
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        sharedPrefs = getSharedPreferences(PREFERENCES, MODE_PRIVATE)
        when (sharedPrefs?.getString(THEME_KEY, "")) {
            LIGHT_THEME -> darkTheme = false
            DARK_THEME -> darkTheme = true
        }
        setTheme(darkTheme)
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled
        setTheme(darkThemeEnabled)
    }

    private fun setTheme(darkThemeEnabled: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                saveTheme(DARK_THEME)
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                saveTheme(LIGHT_THEME)
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }

    private fun saveTheme(theme: String) {
        sharedPrefs?.edit()
            ?.putString(THEME_KEY, theme)
            ?.apply()
    }
    companion object {
        const val PREFERENCES = "preferences"
        const val SEARCHED_TRACKS_KEY = "key_for_searched_tracks"
        private const val THEME_KEY = "key_for_theme"
        private const val LIGHT_THEME = "light"
        private const val DARK_THEME = "dark"
        var darkTheme = false
        var sharedPrefs: SharedPreferences? = null
    }
}