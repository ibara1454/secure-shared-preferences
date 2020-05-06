package com.github.ibara1454.secure_shared_preferences.shared_preferences

import android.content.SharedPreferences
import com.github.ibara1454.secure_shared_preferences.cipher.*
import com.github.ibara1454.secure_shared_preferences.compose

// TODO: replace this exception by domain specific exception
internal class SymmetricKeyEncryptedSharedPreferences @Throws(Exception::class) constructor(
    storage: SharedPreferences,
    key: SecretKey
) : SharedPreferences by EncryptedSharedPreferences(
    storage = storage,
    encrypter = StringEncrypter(Base64Encrypter()::encrypt compose AESEncrypter(key)::encrypt),
    decrypter = StringDecrypter(AESDecrypter(key)::decrypt compose Base64Decrypter()::decrypt)
)
