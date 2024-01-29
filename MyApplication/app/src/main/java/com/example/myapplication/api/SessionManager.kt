package com.example.myapplication.api

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private var prefs: SharedPreferences = context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)

    companion object {
        const val USER_TOKEN = "user_token"
        const val USER_NAME = "user_name"
        const val ID_USER = "id_user" // Zmieniono na String, aby uniknąć pomyłek z wartością domyślną
        const val TRANING_LVL = "traning_level"
    }

    var authToken: String?
        get() = prefs.getString(USER_TOKEN, null)
        set(value) = prefs.edit().putString(USER_TOKEN, value).apply()

    var userName: String?
        get() = prefs.getString(USER_NAME, null)
        set(value) = prefs.edit().putString(USER_NAME, value).apply()

    var id: Int
        get() = prefs.getInt(ID_USER, -1) // Użyj -1 jako wartość domyślną, wskazującą na "brak wartości"
        set(value) = prefs.edit().putInt(ID_USER, value).apply()

    var traning_level: String?
        get() = prefs.getString(TRANING_LVL, null)
        set(value) = prefs.edit().putString(TRANING_LVL, value).apply()

}
