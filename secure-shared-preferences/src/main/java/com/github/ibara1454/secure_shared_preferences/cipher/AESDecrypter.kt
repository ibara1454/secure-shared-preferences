package com.github.ibara1454.secure_shared_preferences.cipher

import com.github.ibara1454.secure_shared_preferences.exception.*
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec


internal class AESDecrypter
    @Throws(InvalidSpecificationException::class) constructor(secretKey: ByteArray):
    Decrypter<ByteArray>
{

    private val decrypter: Cipher
    private val secretKeySpec = SecretKeySpec(secretKey, "AES")

    init {
        try {
            // https://docs.oracle.com/javase/jp/8/docs/api/javax/crypto/Cipher.html
            // https://tools.ietf.org/html/rfc5652#section-6.3
            decrypter = Cipher.getInstance("AES/CBC/PKCS5Padding")
        } catch (e: java.security.NoSuchAlgorithmException) {
            throw InvalidSpecificationException()
        } catch (e: javax.crypto.NoSuchPaddingException) {
            throw InvalidSpecificationException()
        }
    }

    @Throws(
        InvalidKeyException::class,
        InvalidIVException::class,
        IllegalBlockSizeException::class,
        DecryptionException::class
    )
    override fun decrypt(text: ByteArray): ByteArray {
        // TODO: use more efficient way to split iv and crypto
        // Take first 16 bytes for iv
        val iv = text.take(16).toByteArray()
        val crypto = text.drop(16).toByteArray()

        // Initialize decrypter with given secret key and iv
        try {
            decrypter.init(Cipher.DECRYPT_MODE, secretKeySpec, IvParameterSpec(iv))
        } catch (e: java.security.InvalidKeyException) {
            // If secret key is not 16-byte long
            throw InvalidKeyException()
        } catch (e: java.security.InvalidAlgorithmParameterException) {
            // If iv is not 16-byte long
            throw InvalidIVException()
        }

        // Decrypt crypto text
        return try {
            decrypter.doFinal(crypto)
        } catch (e: javax.crypto.IllegalBlockSizeException) {
            // If the crypto is not a multiple of 16
            throw IllegalBlockSizeException()
        } catch (e: javax.crypto.BadPaddingException) {
            // If the crypto is not in a correct form
            throw DecryptionException()
        }
    }
}
