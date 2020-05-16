package com.github.ibara1454.secure_shared_preferences.shared_preferences

import android.content.Context
import android.content.SharedPreferences
import androidx.annotation.VisibleForTesting
import com.github.ibara1454.secure_shared_preferences.secret.UUIDGenerator
import com.github.ibara1454.secure_shared_preferences.shared_preferences.encrypted.EncryptedSharedPreferencesFactory
import java.io.IOException

class SecureSharedPreferencesFactory(
    private val context: Context,
    private val factory: SharedPreferencesFactory = EncryptedSharedPreferencesFactory(context),
    private val config: SecuredSharedPreferences = SecuredSharedPreferences(
        factory.create(CONFIG_NAME, Context.MODE_PRIVATE)
    )
) : SharedPreferencesFactory {
    @VisibleForTesting
    fun getUUIDGenerator(): UUIDGenerator = UUIDGenerator()

    @VisibleForTesting
    fun getOrCreateMappedName(key: String): String {
        return config.getMappedName(key) ?: getUUIDGenerator().generate().also {
            config.setMappedName(key, it)
        }
    }

    override fun create(name: String, mode: Int): SharedPreferences {
        val mappedName = getOrCreateMappedName(name)
        return factory.create(mappedName, mode)
    }

    class SecuredSharedPreferences(private val preferences: SharedPreferences) {
        fun getMappedName(key: String): String? {
            return preferences.getString(key, null)
        }

        fun setMappedName(key: String, value: String) {
            // Important: use synchronized `commit` instead of `apply` to make sure any
            //  failure during saving this key would not be ignored.
            val result = preferences
                .edit()
                .putString(key, value)
                .commit()
            // TODO: throw custom exception
            if (!result) throw IOException()
        }
    }

    companion object {
        // The name of preferences of config
        // echo -n "SecureSharedPreferencesFactory_config" | sha256sum
        private const val CONFIG_NAME =
            "f4f9f04c3f5941b8f3de2b8b5b3f00c22d9141a4488798c2800937b6a084669f"
    }
}
