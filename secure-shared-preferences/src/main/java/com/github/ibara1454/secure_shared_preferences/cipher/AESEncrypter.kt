package com.github.ibara1454.secure_shared_preferences.cipher

import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

internal class AESEncrypter(secretKey: ByteArray): Encrypter<ByteArray> {
    // TODO: generate iv dynamically
    private val iv = "0000000000000000".toByteArray()

    private val encrypter = Cipher.getInstance("AES/CBC/PKCS5Padding")

    init {
        val ivSpec = IvParameterSpec(iv)
        val secretKeySpec = SecretKeySpec(secretKey, "AES")
        encrypter.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivSpec)
    }

    override fun encrypt(text: ByteArray): ByteArray =
        encrypter.doFinal(text)
}
