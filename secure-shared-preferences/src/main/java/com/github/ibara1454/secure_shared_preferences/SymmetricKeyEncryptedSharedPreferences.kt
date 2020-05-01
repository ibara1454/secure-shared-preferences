package com.github.ibara1454.secure_shared_preferences

import android.content.Context
import android.content.SharedPreferences
import androidx.annotation.VisibleForTesting
import com.github.ibara1454.secure_shared_preferences.cipher.*
import java.io.IOException

internal class SymmetricKeyEncryptedSharedPreferencesFactory : PreferencesFactory {
    // Generator for generating 128-bit length secret key
    @VisibleForTesting
    fun getSecretGenerator() = SecretGenerator()

    // TODO: replace this exception by domain specific's exception
    @Throws(IOException::class)
    @VisibleForTesting
    fun getSymmetricKeyEncryptedSharedPreferences(name: String, key: SecretKey, context: Context): SharedPreferences {
        // Use encrypted shared preferences to save configurations
        val preferences = context.getSharedPreferences(name, Context.MODE_PRIVATE)
        return SymmetricKeyEncryptedSharedPreferences(preferences, key)
    }

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
        // Instantiate the global config to get the saved secret key
        val config = SymmetricKeyEncryptedSharedPreferencesConfig(
            getSymmetricKeyEncryptedSharedPreferences(CONFIG_NAME, configSecretKey, context)
        )
        // Read a existing secret key from config.
        // If there is no secret key exists. Then generates a new key and save it into config.
        val key = config.secretKey ?: getSecretGenerator().generate().also {
            // Save new key into config
            config.secretKey = it
        }
        return getSymmetricKeyEncryptedSharedPreferences(name, key, context)
    }

    companion object {
        // The name of preferences of config
        // echo -n "SymmetricKeyEncryptedSharedPreferencesFactory_config" | sha256sum
        private const val CONFIG_NAME = "41dd1ef45721398d6633e907363d91bc266a476f4cb4bef61f8c8b669b1de982"
        // The secret key for encrypting this config preferences. There is no way to hide this key without using KeyStore.
        private val configSecretKey: ByteArray = byteArrayOf(71, -6, -39, 122, -19, -86, 90, 14, -123, 86, -65, -35, -56, -4, -51, -95)
    }

    @VisibleForTesting
    class SymmetricKeyEncryptedSharedPreferencesConfig(private val preferences: SharedPreferences) {
        var secretKey: SecretKey?
            get() = preferences.getString(KEY_NAME, "").run {
                    if (this == null || this.isEmpty()) {
                        null
                    } else {
                        // Convert string (use utf-8 encode) to byte array
                        toByteArray(Charsets.UTF_8)
                    }
                }
            // TODO: replace this exception by domain specific exception
            @Throws(IOException::class)
            set(value) {
                // Important: use synchronized `commit` instead of `apply` to make sure the writing doesn't fail
                val result =
                    preferences.edit()
                        // Convert byte array to string (use utf-8 encode)
                        .putString(KEY_NAME, value?.toString(Charsets.UTF_8))
                        .commit()
                // TODO: throw custom exception
                if (!result) throw IOException()
            }

        companion object {
            // The key name of the secret key which is used on encrypting another secret keys
            private const val KEY_NAME = "secret_key"
        }
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
