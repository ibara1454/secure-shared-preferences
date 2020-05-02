package com.github.ibara1454.secure_shared_preferences

import android.content.Context
import android.content.SharedPreferences
import androidx.annotation.VisibleForTesting
import com.github.ibara1454.secure_shared_preferences.cipher.*
import java.io.IOException

internal object SecretKeys {
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

    /**
     * The configuration class provides accessors to read / save secret keys.
     *
     * @property preferences Any [SharedPreferences] instance. This class use [SharedPreferences] to
     *  read and save secret key, so [preferences] should be an encrypted [SharedPreferences], or
     *  the secret key will be saved with plain text.
     * @property encrypter An [Encrypter] to convert byte array (the secret key) to the format
     *  could be encoded to string. The default encrypter is [Base64Encrypter].
     * @property decrypter An [Decrypter] to convert the byte array encrypted by [encrypter] to the
     *  original byte array. The default decrypter is [Base64Decrypter].
     */
    @VisibleForTesting
    internal class SecretKeysConfig(
        private val preferences: SharedPreferences,
        private val encrypter: Encrypter<SecretKey> = Base64Encrypter(),
        private val decrypter: Decrypter<SecretKey> = Base64Decrypter()
    ) {
        var secretKey: SecretKey?
            // TODO: replace this exception by domain specific exception
            @Throws(IOException::class)
            set(value) {
                // Convert byte array to string.
                // Note that not every byte array can be convert to the specific encoding string
                //  (would be garbled), so we have to convert byte array to an
                //  convertible format (via encrypter) and then convert it to string.
                val secret = value?.let(encrypter::encrypt)?.toString(charset)
                // Important: use synchronized `commit` instead of `apply` to make sure any
                //  failure during saving this key would not be ignored.
                val result = preferences.edit()
                        .putString(KEY_NAME, secret)
                        .commit()
                // TODO: throw custom exception
                if (!result) throw IOException()
            }
            get() = preferences.getString(KEY_NAME, "").run {
                if (this == null || this.isEmpty()) {
                    null
                } else {
                    // Convert string to byte array.
                    // Since the string is encrypted by the given encrypter, we have to decrypt it
                    //  before convert to string.
                    decrypter.decrypt(this.toByteArray(charset))
                }
            }

        companion object {
            private val charset = Charsets.UTF_8
            // The key name of the secret key which is used on encrypting another secret keys
            private const val KEY_NAME = "secret_key"
        }
    }
}
