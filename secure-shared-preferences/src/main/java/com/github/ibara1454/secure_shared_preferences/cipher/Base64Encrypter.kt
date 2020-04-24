package com.github.ibara1454.secure_shared_preferences.cipher

internal class Base64Encrypter: Encrypter<ByteArray> {
    override fun encrypt(text: ByteArray): ByteArray =
        android.util.Base64.encode(text, android.util.Base64.NO_WRAP)
}
