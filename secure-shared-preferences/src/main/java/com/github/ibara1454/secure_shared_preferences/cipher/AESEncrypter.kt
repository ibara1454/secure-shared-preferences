package com.github.ibara1454.secure_shared_preferences.cipher

import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

internal class AESEncrypter(secretKey: ByteArray): Encrypter<ByteArray> {
    // https://docs.oracle.com/javase/jp/8/docs/api/javax/crypto/Cipher.html
    // https://tools.ietf.org/html/rfc5652#section-6.3
    private val encrypter = Cipher.getInstance("AES/CBC/PKCS5Padding")

    private val secretKeySpec = SecretKeySpec(secretKey, "AES")

    override fun encrypt(text: ByteArray): ByteArray {
        // Initialize the encrypter without initial vector - this will make initial vector be
        //  auto-generated.
        encrypter.init(Cipher.ENCRYPT_MODE, secretKeySpec)
        val iv = encrypter.iv
        val crypto = encrypter.doFinal(text)
        return iv + crypto
    }
}
