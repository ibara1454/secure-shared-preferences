package com.github.ibara1454.secure_shared_preferences

import android.content.Context
import android.content.SharedPreferences
import com.github.ibara1454.secure_shared_preferences.cipher.*
import java.io.IOException

internal class SymmetricKeyEncryptedSharedPreferencesFactory : PreferencesFactory {
    /**
     * Create a [SymmetricKeyEncryptedSharedPreferences].
     *
     * @param name Name of preferences.
     * @param mode Operating mode. This parameter is same as the mode parameter in normal
     *  SharedPreferences.
     * [Context.getSharedPreferences].
     * @return Returns the encrypted [SharedPreferences].
     */
    // TODO: replace this exception by domain specific exception
    @Throws(IOException::class)
    @Synchronized
    override fun create(name: String, mode: Int, context: Context): SharedPreferences {
        val key = SecretKeys.getOrCreate(context)
        val preferences = context.getSharedPreferences(name, Context.MODE_PRIVATE)
        return SymmetricKeyEncryptedSharedPreferences(preferences, key)
    }
}

// TODO: replace this exception by domain specific exception
internal class SymmetricKeyEncryptedSharedPreferences @Throws(Exception::class) constructor(
    storage: SharedPreferences,
    key: SecretKey
) : SharedPreferences by EncryptedSharedPreferences(
    storage = storage,
    encrypter = StringEncrypter(Base64Encrypter()::encrypt compose AESEncrypter(key)::encrypt),
    decrypter = StringDecrypter(AESDecrypter(key)::decrypt compose Base64Decrypter()::decrypt)
) {
    // Add SymmetricKeyEncryptedSharedPreferences creator by class delegation
    companion object : PreferencesFactory by SymmetricKeyEncryptedSharedPreferencesFactory()
}
