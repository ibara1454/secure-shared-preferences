package com.github.ibara1454.secure_shared_preferences

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import androidx.annotation.VisibleForTesting
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import java.io.IOException

internal class SecureSharedPreferencesFactory(private val context: Context) {
    @VisibleForTesting
    val topEncryptType: EncryptType =
        // https://stackoverflow.com/questions/3993924/get-android-api-level-of-phone-currently-running-my-application
        when (Build.VERSION.SDK_INT) {
            in Build.VERSION_CODES.BASE..Build.VERSION_CODES.LOLLIPOP_MR1 -> EncryptType.AES
            else -> EncryptType.KEYSTORE
        }

    @Throws(IOException::class)
    fun create(name: String, mode: Int): SharedPreferences {
        val config = SecureSharedPreferencesConfig(context)
        val type: EncryptType = config.currentEncryptType ?: topEncryptType.also {
            config.currentEncryptType = it
        }

        return when (type) {
            EncryptType.NORMAL -> context.getSharedPreferences(name, mode)
            EncryptType.AES ->
                SymmetricKeyEncryptedSharedPreferences.create(name, mode, context)
            EncryptType.KEYSTORE -> {
                val masterKeys = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
                EncryptedSharedPreferences.create(
                    name,
                    masterKeys,
                    context,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
                )
            }
        }
    }

    class SecureSharedPreferencesConfig(context: Context) {
        @VisibleForTesting
        val config: SharedPreferences = context.getSharedPreferences(CONFIG_NAME, Context.MODE_PRIVATE)

        @VisibleForTesting
        var currentEncryptType: EncryptType?
            @Throws(IOException::class)
            set(value) {
                // Important: use synchronized `commit` instead of `apply` to make sure the writing doesn't fail
                val result = config
                    .edit()
                    .putString(ENCRYPT_TYPE_KEY, value?.name)
                    .commit()
                // TODO: throw custom exception
                if (!result) throw IOException()
            }
            get() = config.getString(ENCRYPT_TYPE_KEY, "").run {
                if (this == null || this.isEmpty()) {
                    null
                } else {
                    EncryptType.valueOf(this)
                }
            }

        companion object {
            // The name of preferences of config
            // echo -n "SecureSharedPreferencesFactory_config" | sha256sum
            private const val CONFIG_NAME = "f4f9f04c3f5941b8f3de2b8b5b3f00c22d9141a4488798c2800937b6a084669f"
            private const val ENCRYPT_TYPE_KEY = "encrypt_type"
        }
    }


}
