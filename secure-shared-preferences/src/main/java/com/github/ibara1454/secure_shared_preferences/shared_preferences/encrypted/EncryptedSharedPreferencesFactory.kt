package com.github.ibara1454.secure_shared_preferences.shared_preferences.encrypted

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.github.ibara1454.secure_shared_preferences.shared_preferences.SharedPreferencesFactory

/**
 * The factory class which creates the encrypted shared preferences.
 *
 * Note that this class needs to save the secret key into keystore, so you can only use this class
 * on SDK version 22+.
 * @param context any application context or activity context.
 */
@RequiresApi(Build.VERSION_CODES.M)
class EncryptedSharedPreferencesFactory(private val context: Context):
    SharedPreferencesFactory {
    /**
     * Creates encrypted shared preferences.
     *
     * @param name name of preferences.
     * @param mode operating mode. Note that this parameter does not working and will be fixed to
     *  [Context.MODE_PRIVATE].
     * @return encrypted [SharedPreferences].
     */
    override fun create(name: String, mode: Int): SharedPreferences {
        // implements the encrypted shared preferences by
        //  androidx.security.crypto.EncryptedSharedPreferences.
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
