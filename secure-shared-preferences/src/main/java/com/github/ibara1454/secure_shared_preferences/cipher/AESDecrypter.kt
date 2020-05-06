package com.github.ibara1454.secure_shared_preferences.cipher

import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

internal class AESDecrypter(secretKey: ByteArray): Decrypter<ByteArray> {
    // https://docs.oracle.com/javase/jp/8/docs/api/javax/crypto/Cipher.html
    // https://tools.ietf.org/html/rfc5652#section-6.3
    private val decrypter = Cipher.getInstance("AES/CBC/PKCS5Padding")

    private val secretKeySpec = SecretKeySpec(secretKey, "AES")

    // TODO: throw exception when error occurred
    override fun decrypt(text: ByteArray): ByteArray {
        // TODO: use more efficient way to split iv and crypto
        // Take first 16 bytes for iv
        val iv = text.take(16).toByteArray()
        val crypto = text.drop(16).toByteArray()
        decrypter.init(Cipher.DECRYPT_MODE, secretKeySpec, IvParameterSpec(iv))
        return decrypter.doFinal(crypto)
    }
}
