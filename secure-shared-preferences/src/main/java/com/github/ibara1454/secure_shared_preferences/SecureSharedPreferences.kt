@file:JvmName("SecureSharedPreferences")

package com.github.ibara1454.secure_shared_preferences

import android.content.Context
import android.content.SharedPreferences
import com.github.ibara1454.secure_shared_preferences.shared_preferences.SecureSharedPreferencesFactory
import com.github.ibara1454.secure_shared_preferences.shared_preferences.SharedPreferencesFactory

fun Context.getSecureSharedPreferences(name: String, mode: Int): SharedPreferences {
    val factory: SharedPreferencesFactory = SecureSharedPreferencesFactory(this)
    return factory.create(name, mode)
}
