package com.github.ibara1454.secure_shared_preferences

import android.content.Context
import android.content.SharedPreferences
import androidx.annotation.VisibleForTesting
import java.io.IOException

object SecretKeys {
    // Generator for generating 128-bit length secret key
    @VisibleForTesting
    fun getSecretGenerator() = SecretGenerator()

    @VisibleForTesting
    fun getConfig(preferences: SharedPreferences): SecretKeysConfig {
        return SecretKeysConfig(
            SymmetricKeyEncryptedSharedPreferences(preferences, configSecretKey)
        )
    }

    // TODO: replace this exception by domain specific exception
    @Throws(IOException::class)
    fun getOrCreate(context: Context): SecretKey {
        val preferences = context.getSharedPreferences(CONFIG_NAME, Context.MODE_PRIVATE)
        val config = getConfig(preferences)
        // Read a existing secret key from config.
        // If there is no secret key exists. Then generates a new key and save it into config.
        return config.secretKey ?: getSecretGenerator().generate().also {
            // Save new key into config
            config.secretKey = it
        }
    }

    // The name of preferences of config
    // echo -n "SymmetricKeyEncryptedSharedPreferencesFactory_config" | sha256sum
    private const val CONFIG_NAME = "ab6a6d8c47c1613694850bb67eaba9545e87b63629acc85159346cee3e646d76"
    // The secret key for encrypting this config preferences. There is no way to hide this key without using KeyStore.
    private val configSecretKey: ByteArray = byteArrayOf(71, -6, -39, 122, -19, -86, 90, 14, -123, 86, -65, -35, -56, -4, -51, -95)

    @VisibleForTesting
    class SecretKeysConfig(private val preferences: SharedPreferences) {
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
