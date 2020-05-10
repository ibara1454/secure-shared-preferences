package com.github.ibara1454.secure_shared_preferences.shared_preferences.safe

import android.content.SharedPreferences
import com.github.ibara1454.secure_shared_preferences.cipher.*
import com.github.ibara1454.secure_shared_preferences.compose
import com.github.ibara1454.secure_shared_preferences.secret.SecretKey
import com.github.ibara1454.secure_shared_preferences.shared_preferences.EncryptableSharedPreferences

// TODO: replace this exception by domain specific exception
internal class SafeSharedPreferences @Throws(Exception::class) constructor(
    preferences: SharedPreferences,
    key: SecretKey
) : SharedPreferences by EncryptableSharedPreferences(
    preferences = preferences,
    encrypter = StringEncrypter(Base64Encoder()::encode compose AESEncrypter(key)::encrypt),
    decrypter = StringDecrypter(AESDecrypter(key)::decrypt compose Base64Decoder()::decode)
)
