package com.github.ibara1454.secure_shared_preferences.shared_preferences

import android.content.SharedPreferences

/**
 * The standard interface defines a [create] method to return [SharedPreferences].
 */
interface SharedPreferencesFactory {
    /**
     * Create a [SharedPreferences].
     *
     * @param name name of preferences.
     * @param mode operating mode. This parameter is same as the mode parameter in normal
     *  [SharedPreferences].
     * @return [SharedPreferences].
     */
    fun create(name: String, mode: Int): SharedPreferences
}
