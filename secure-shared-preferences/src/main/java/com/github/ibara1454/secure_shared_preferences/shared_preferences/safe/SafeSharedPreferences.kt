package com.github.ibara1454.secure_shared_preferences.shared_preferences.safe

import android.content.Context
import android.content.SharedPreferences
import com.github.ibara1454.secure_shared_preferences.cipher.*
import com.github.ibara1454.secure_shared_preferences.*
import com.github.ibara1454.secure_shared_preferences.secret.SecretKey
import com.github.ibara1454.secure_shared_preferences.shared_preferences.EncryptableSharedPreferences

// TODO: replace this exception by domain specific exception
internal class SafeSharedPreferences @Throws(Exception::class) constructor(
    context: Context,
    name: String,
    mode: Int,
    key: SecretKey
) : SharedPreferences by EncryptableSharedPreferences(
    preferences = context.getSharedPreferences(name, mode),
    // TODO: consider using AES-GCB-GIV instead to get stronger 'nonce reuse resistance'.
    prefNameEncrypter = StringEncrypter(Base64Encoder()::encode compose FixedIVAESCBCEncrypter(key, sha256(name).copyOf(16))::encrypt),
    prefNameDecrypter = StringDecrypter(FixedIVAESCBCDecrypter(key, sha256(name).copyOf(16))::decrypt compose Base64Decoder()::decode),
    prefValueEncrypter = StringEncrypter(Base64Encoder()::encode compose AESEncrypter(key)::encrypt),
    prefValueDecrypter = StringDecrypter(AESDecrypter(key)::decrypt compose Base64Decoder()::decode)
)
