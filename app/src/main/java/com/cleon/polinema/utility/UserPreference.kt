package com.cleon.polinema.utility

import android.content.Context

class UserPreference(context: Context) {
    private val preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME = "user_pref"
        private const val USERNAME = "username"
        private const val JABATAN = "jabatan"
    }

    // Menyimpan data username dan jabatan
    fun saveUser(username: String, jabatan: String) {
        val editor = preferences.edit()
        editor.putString(USERNAME, username)
        editor.putString(JABATAN, jabatan)
        editor.apply()
    }

    // Mengambil username
    fun getUsername(): String? {
        return preferences.getString(USERNAME, null)
    }

    // Mengambil jabatan
    fun getJabatan(): String? {
        return preferences.getString(JABATAN, null)
    }

    // Menghapus data login (logout)
    fun clearUser() {
        val editor = preferences.edit()
        editor.clear().apply()
    }
}
