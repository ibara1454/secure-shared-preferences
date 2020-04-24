package com.github.ibara1454.secure_shared_preferences.cipher

import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

internal class AESDecrypter(secretKey: ByteArray): Decrypter<ByteArray> {
    // TODO: generate iv dynamically
    private val iv = "0000000000000000".toByteArray()

    private val decrypter = Cipher.getInstance("AES/CBC/PKCS5Padding")

    init {
        val ivSpec = IvParameterSpec(iv)
        val secretKeySpec = SecretKeySpec(secretKey, "AES")
        decrypter.init(Cipher.DECRYPT_MODE, secretKeySpec, ivSpec)
    }

    override fun decrypt(text: ByteArray): ByteArray =
        decrypter.doFinal(text)
}
