package com.github.ibara1454.secure_shared_preferences

import android.content.Context
import android.content.SharedPreferences
import androidx.annotation.VisibleForTesting
import com.github.ibara1454.secure_shared_preferences.cipher.*
import java.io.IOException

internal class SymmetricKeyEncryptedSharedPreferencesFactory(private val context: Context) {
    @VisibleForTesting
    val config: SharedPreferences = run {
        // TODO: change configKey
        val configKey: ByteArray = "0000000000000000".toByteArray(Charsets.UTF_8)
        val encrypter = StringEncrypter(Base64Encrypter()::encrypt compose AESEncrypter(configKey)::encrypt)
        val decrypter = StringDecrypter(AESDecrypter(configKey)::decrypt compose Base64Decrypter()::decrypt)
        val storage = context.getSharedPreferences(CONFIG_NAME, Context.MODE_PRIVATE)
        // Use encrypted shared preferences to save configurations
        SymmetricKeyEncryptedSharedPreferences(storage, encrypter, decrypter)
    }

    @VisibleForTesting
    var secretKey: SecretKey?
        get() = config.getString(KEY_NAME, "").run {
            if (this == null || this.isEmpty()) {
                null
            } else {
                // Convert string (use utf-8 encode) to byte array
                toByteArray(Charsets.UTF_8)
            }
        }
        @Throws(IOException::class)
        set(value) {
            // Important: use synchronized `commit` instead of `apply` to make sure the writing doesn't fail
            val result = config
                .edit()
                // Convert byte array to string (use utf-8 encode)
                .putString(KEY_NAME, value?.toString(Charsets.UTF_8))
                .commit()
            // TODO: throw custom exception
            if (!result) throw IOException()
        }

    // Generator for generating 128-bit length secret key
    @VisibleForTesting
    val secretGenerator = SecretGenerator()

    /**
     * @param name Name of preferences.
     * @param mode Operating mode. This value is same as the mode parameter in
     * [Context.getSharedPreferences].
     * @return Returns the encrypted [SymmetricKeyEncryptedSharedPreferences].
     */
    @Throws(IOException::class)
    @Synchronized
    fun create(name: String, mode: Int): SymmetricKeyEncryptedSharedPreferences {
        // Read a existing secret key from config or generate a new secret key
        val key = secretKey ?: secretGenerator.generate().also {
            // Save new key into config
            secretKey = it
        }

        val encrypter = StringEncrypter(Base64Encrypter()::encrypt compose AESEncrypter(key)::encrypt)
        val decrypter = StringDecrypter(AESDecrypter(key)::decrypt compose Base64Decrypter()::decrypt)
        // Get the unencrypted normal shared preferences
        val storage = context.getSharedPreferences(name, mode)
        return SymmetricKeyEncryptedSharedPreferences(storage, encrypter, decrypter)
    }

    companion object {
        // The name of preferences of config
        // echo -n "SymmetricKeyEncryptedSharedPreferencesFactory_config" | sha256sum
        private const val CONFIG_NAME = "41dd1ef45721398d6633e907363d91bc266a476f4cb4bef61f8c8b669b1de982"
        // The key name of the secret key which is used on encrypting another secret keys
        private const val KEY_NAME = "secret_key"
    }
}
