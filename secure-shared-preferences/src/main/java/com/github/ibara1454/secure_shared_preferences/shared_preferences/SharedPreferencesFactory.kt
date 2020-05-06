package com.github.ibara1454.secure_shared_preferences.shared_preferences

import android.content.SharedPreferences

interface SharedPreferencesFactory {
    fun create(name: String, mode: Int): SharedPreferences
}
