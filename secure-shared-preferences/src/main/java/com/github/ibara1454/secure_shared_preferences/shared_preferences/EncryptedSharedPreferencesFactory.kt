package com.github.ibara1454.secure_shared_preferences.shared_preferences

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys

class EncryptedSharedPreferencesFactory(private val context: Context): SharedPreferencesFactory {
    override fun create(name: String, mode: Int): SharedPreferences {
        val masterKeys = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
        // TODO: catch exceptions thrown from EncryptedSharedPreferences.create
        return EncryptedSharedPreferences.create(
            name,
            masterKeys,
            context,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }
}
