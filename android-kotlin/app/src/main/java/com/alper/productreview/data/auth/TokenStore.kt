package com.alper.productreview.data.auth

import android.content.Context

class TokenStore(context: Context) {
    private val prefs = context.getSharedPreferences("auth", Context.MODE_PRIVATE)

    fun saveToken(token: String) {
        prefs.edit().putString("jwt", token).apply()
    }

    fun getToken(): String? = prefs.getString("jwt", null)

    fun clear() {
        prefs.edit().remove("jwt").apply()
    }
}
