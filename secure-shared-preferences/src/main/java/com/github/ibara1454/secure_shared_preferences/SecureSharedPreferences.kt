@file:JvmName("SecureSharedPreferences")

package com.github.ibara1454.secure_shared_preferences

import android.content.Context
import android.content.SharedPreferences
import com.github.ibara1454.secure_shared_preferences.shared_preferences.SecureSharedPreferencesFactory
import com.github.ibara1454.secure_shared_preferences.shared_preferences.SharedPreferencesFactory

/**
 * Create a [SharedPreferences] with encrypted contents and encrypted file name.
 *
 * This function could return different encryption level for [SharedPreferences], which is decided
 * by the environments: SDK version and device-specific constraints. There are 3 levels for
 * encryption:
 * - Level1, contents are encrypted with the AES algorithm.
 * - Level2, contents are encrypted with the AES algorithm.
 * - Level3, contents are saved as plain data.
 *
 * As level1, all the contents in shared preferences are encrypted with AES algorithm,
 * and the secret key is saved in KeyStore. This level is only supported on SDK version 23+ OS.
 * The shared preferences function returns is the same instance as
 * [androidx.security.crypto.EncryptedSharedPreferences], which is created by Androidx security
 * library.
 *
 * As level2, all the contents in shared preferences are encrypted with AES algorithm,
 * and the secret key is obfuscated and saved in another [SharedPreferences]. This level is
 * supported on all SDK versions.
 *
 * As level3, all the contents in shared preferences are not encrypted but saved as plain data.
 * Normally, this level would not be chose if level3 or level2 encryption works. It is a
 * substitution of level1 and level2.
 *
 * The encryption levels above all provide file name encryption, which is based on random UUID.
 * When the new file name is passed to this function, if the name is not exists,
 * a new UUID will be generated, and will replaced the origin name.
 *
 * Usage:
 *
 * In Kotlin,
 *
 * ```kotlin
 * val context = applicationContext
 * val preferences = context.getSecuredSharedPreferences("name", Context.MODE_PRIVATE)
 * ```
 *
 * or in Java,
 *
 * ```java
 * Context context = getApplicationContext();
 * SharedPreferences preferences = SecureSharedPreferences.getSecureSharedPreferences(
 *     context,
 *     "name",
 *     Context.MODE_PRIVATE
 * );
 * ```
 *
 * @receiver any application context or activity context.
 * @param name name of preferences.
 * @param mode operating mode. This parameter is same as the mode parameter in normal
 *  SharedPreferences. Note that this parameter does not working and will be fixed to
 *  [Context.MODE_PRIVATE] after SDK version 22.
 * @return Encrypted [SharedPreferences].
 */
fun Context.getSecureSharedPreferences(name: String, mode: Int): SharedPreferences {
    val factory: SharedPreferencesFactory = SecureSharedPreferencesFactory(this)
    return factory.create(name, mode)
}
