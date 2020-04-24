package com.github.ibara1454.secure_shared_preferences

import android.content.Context
import android.content.SharedPreferences

fun Context.getSecureSharedPreferences(name: String, mode: Int): SharedPreferences {
    val factory = SecureSharedPreferencesFactory(this)
    return factory.create(name, mode)
}
