package com.github.ibara1454.secure_shared_preferences.shared_preferences.encrypted

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import androidx.annotation.VisibleForTesting
import com.github.ibara1454.secure_shared_preferences.shared_preferences.SharedPreferencesFactory
import com.github.ibara1454.secure_shared_preferences.shared_preferences.keystore.KeystoreEncryptedSharedPreferencesFactory
import com.github.ibara1454.secure_shared_preferences.shared_preferences.safe.SafeSharedPreferencesFactory
import java.io.IOException

internal class EncryptedSharedPreferencesFactory(
    private val context: Context,
    private val config: EncryptedSharedPreferencesConfig = EncryptedSharedPreferencesConfig(
        context
    )
) : SharedPreferencesFactory {
    @VisibleForTesting
    val topEncryptType: EncryptType
        get() =
            // https://stackoverflow.com/questions/3993924/get-android-api-level-of-phone-currently-running-my-application
            when (Build.VERSION.SDK_INT) {
                in Build.VERSION_CODES.BASE..Build.VERSION_CODES.LOLLIPOP_MR1 -> EncryptType.SAFE
                else -> EncryptType.KEYSTORE
            }

    // Suppress the lint error "Error: Call requires API level 23"
    @SuppressLint("NewApi")
    @VisibleForTesting
    // TODO: replace this exception by domain specific exception
    @Throws(IOException::class)
    fun create(name: String, mode: Int, type: EncryptType): SharedPreferences =
        when (type) {
            EncryptType.NONE -> context.getSharedPreferences(name, mode)
            EncryptType.SAFE ->
                // TODO: catch exceptions thrown from create
                SafeSharedPreferencesFactory(
                    context
                ).create(name, mode)
            // The class KeystoreEncryptedSharedPreferencesFactory only be used on version greater
            //  than or equal to M.
            EncryptType.KEYSTORE -> {
                // TODO: catch exceptions thrown from EncryptedSharedPreferences.create
                KeystoreEncryptedSharedPreferencesFactory(
                    context
                ).create(name, mode)
            }
        }

    @VisibleForTesting
    // TODO: replace this exception by domain specific exception
    @Throws(IOException::class)
    fun tryCreate(
        name: String,
        mode: Int,
        type: EncryptType
    ): Pair<SharedPreferences, EncryptType> =
        try {
            create(name, mode, type) to type
        } catch (e: IOException) {
            // If type is equals to `Normal`, there is no lower encryption type and then throw
            //  exception.
            if (type == EncryptType.NONE) {
                // TODO: replace this exception by domain specific exception
                throw e
            }
            // Otherwise, then retry creation with lower encryption type.
            tryCreate(name, mode, type.downgrade())
        }

    // TODO: replace this exception by domain specific exception
    @Throws(IOException::class)
    override fun create(name: String, mode: Int): SharedPreferences = synchronized(this::class) {
        val currentType = config.currentEncryptType
        val targetType = currentType ?: topEncryptType
        // Since creation of SharedPreferences need IO performances, not every time the creation
        //  will succeed. It could failed with unexpected exceptions. Some of the exceptions are
        //  recoverable, and some of them are not. Because we don't have an exactly way to
        //  distinguish these two types of exceptions, we use the following logic to create
        //  SharedPreferences:
        //
        //  1. If `currentType` is not null (which means the this creation of SharedPreferences
        //  succeeded at least once), there may have some data are saved by the same EncryptType.
        //  To avoiding any data loss, we have to instantiate SharedPreferences with exactly the
        //  same EncryptType as `currentType`.
        //  2. If `currentType` is null, which means there is no data existing on
        //  old SharedPreferences. Without concern for data losing, we could try creating until we
        //  find a proper EncryptedType.
        return if (currentType != null) {
            create(name, mode, targetType)
        } else {
            // Try creation started with the given `targetType`
            val (preferences, type) = tryCreate(name, mode, targetType)
            // If the creation succeed, then save the proper EncryptedType. Note that even the
            //  creation succeeded, the saving of EncryptedType may failed.
            config.currentEncryptType = type
            preferences
        }
    }

    class EncryptedSharedPreferencesConfig(context: Context) {
        @VisibleForTesting
        val config: SharedPreferences =
            context.getSharedPreferences(CONFIG_NAME, Context.MODE_PRIVATE)

        @VisibleForTesting
        // TODO: replace this exception by domain specific exception
        var currentEncryptType: EncryptType?
            @Throws(IOException::class)
            set(value) = synchronized(this::class) {
                // Important: use synchronized `commit` instead of `apply` to make sure any
                //  failure during saving this key would not be ignored.
                val result = config
                    .edit()
                    .putString(ENCRYPT_TYPE_KEY, value?.name)
                    .commit()
                // TODO: throw custom exception
                if (!result) throw IOException()
            }
            get() = synchronized(this::class) {
                config.getString(ENCRYPT_TYPE_KEY, "").run {
                    if (this == null || this.isEmpty()) {
                        null
                    } else {
                        when (this) {
                            EncryptType.NONE.name -> EncryptType.NONE
                            EncryptType.SAFE.name -> EncryptType.SAFE
                            EncryptType.KEYSTORE.name -> EncryptType.KEYSTORE
                            else -> null
                        }
                    }
                }
            }

        companion object {
            // The name of preferences of config
            // echo -n "EncryptedSharedPreferencesFactory_config" | sha256sum
            private const val CONFIG_NAME =
                "906841c010c008bc0b7e89870f71ddd09e9a41420323d19abe5142609499cef1"
            private const val ENCRYPT_TYPE_KEY = "encrypt_type"
        }
    }
}
