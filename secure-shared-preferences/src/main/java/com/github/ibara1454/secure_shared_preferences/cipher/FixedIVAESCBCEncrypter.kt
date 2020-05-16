package com.github.ibara1454.secure_shared_preferences.cipher

import com.github.ibara1454.secure_shared_preferences.exception.InvalidIVException
import com.github.ibara1454.secure_shared_preferences.exception.InvalidKeyException
import com.github.ibara1454.secure_shared_preferences.exception.InvalidSpecificationException
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

/**
 * The encrypter using AES-CBC algorithm with fixed initial vector.
 *
 * Note that you should not reuse the IV if possible.
 * Reusing the IV is that if two messages begin with the same sequence
 * of bytes then the encrypted messages will also be identical for a few blocks.
 * This leaks data and opens the possibility of some attacks.
 *
 * The class provides a [encrypt] method to encrypt any text.
 * To decrypt the encrypted text, you could use the [AESDecrypter] class.
 *
 * @param secretKey the 16-byte secret key using in encryption.
 * @param iv the 16-byte initial vector using in encryption.
 * @throws InvalidKeyException if the secret key is not 16 bytes.
 * @throws InvalidSpecificationException if the platform does not support the transformation.
 */
class FixedIVAESCBCEncrypter
@Throws(InvalidSpecificationException::class, InvalidKeyException::class, InvalidIVException::class)
constructor(secretKey: ByteArray, iv: ByteArray) : Encrypter<ByteArray> {
    private val encrypter: Cipher

    init {
        // Cipher only support 128 bits key / iv length for AES/CBC
        if (secretKey.size != 16) {
            throw InvalidKeyException()
        }
        // Cipher only support 128 bits key / iv length for AES/CBC
        if (iv.size != 16) {
            throw InvalidIVException()
        }
        try {
            // Set the transformation to instantiate cipher.
            //  Choose 'AES' for algorithm, 'CBC' for mode, and 'PKCS#5' for padding.
            //  For more information:
            //  https://docs.oracle.com/javase/8/docs/api/javax/crypto/Cipher.html
            //  https://tools.ietf.org/html/rfc5652#section-6.3
            // Note that this transformation should be the same as encrypter's
            encrypter = Cipher.getInstance("AES/CBC/PKCS5Padding")
        } catch (e: java.security.NoSuchAlgorithmException) {
            // If such algorithm not exists
            // Shouldn't happen
            throw InvalidSpecificationException()
        } catch (e: javax.crypto.NoSuchPaddingException) {
            // If the padding method could not be used
            // Shouldn't happen
            throw InvalidSpecificationException()
        }
        // The 128-bit length secret key
        val secretKeySpec = SecretKeySpec(secretKey, "AES")
        // The 128-bit length iv
        val ivSpec = IvParameterSpec(iv)
        encrypter.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivSpec)
    }

    /**
     * Encrypt the given plain text.
     *
     * Note that this method will return a byte array which length is a multiple of 16.
     * The length is dependent on the input text. If the length of input text is
     * between 0 and 15, then we will get 16 for crypto. And if the length of input text is
     * between 16 and 31, then we will get 32 for crypto and so on.
     *
     * @param text any text.
     * @returns encrypted text.
     */
    override fun encrypt(text: ByteArray): ByteArray {
        // Encryption should never throw exceptions
        return encrypter.doFinal(text)
    }
}
