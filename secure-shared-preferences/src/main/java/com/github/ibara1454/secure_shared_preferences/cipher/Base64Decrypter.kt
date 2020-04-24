package com.github.ibara1454.secure_shared_preferences.cipher

internal class Base64Decrypter: Decrypter<ByteArray> {
    override fun decrypt(text: ByteArray): ByteArray =
        android.util.Base64.decode(text, android.util.Base64.NO_WRAP)
}
