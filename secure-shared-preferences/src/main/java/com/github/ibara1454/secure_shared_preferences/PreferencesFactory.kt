package com.github.ibara1454.secure_shared_preferences

import android.content.SharedPreferences

interface PreferencesFactory {
    fun create(name: String, mode: Int): SharedPreferences
}
