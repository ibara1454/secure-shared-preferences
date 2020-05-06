package com.github.ibara1454.secure_shared_preferences.shared_preferences

import android.content.Context
import android.content.SharedPreferences
import java.io.IOException

internal class SymmetricKeyEncryptedSharedPreferencesFactory(private val context: Context) {
    /**
     * Create a [SymmetricKeyEncryptedSharedPreferences].
     *
     * @param name Name of preferences.
     * @param mode Operating mode. This parameter is same as the mode parameter in normal
     *  SharedPreferences.
     * @return Returns the encrypted [SharedPreferences].
     */
    // TODO: replace this exception by domain specific exception
    @Throws(IOException::class)
    @Synchronized
    fun create(name: String, mode: Int): SharedPreferences {
        val key = SecretKeys.getOrCreate(context)

        val preferences = context.getSharedPreferences(name, mode)
        return SymmetricKeyEncryptedSharedPreferences(
            preferences,
            key
        )
    }
}
