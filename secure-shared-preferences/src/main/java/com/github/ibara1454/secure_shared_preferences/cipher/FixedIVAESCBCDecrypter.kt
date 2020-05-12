package com.github.ibara1454.secure_shared_preferences.cipher

import com.github.ibara1454.secure_shared_preferences.exception.*
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

/**
 * The decrypter using AES-CBC algorithm with fixed initial vector.
 *
 * Note that you should not reuse the IV if possible.
 * Reusing the IV is that if two messages begin with the same sequence
 * of bytes then the encrypted messages will also be identical for a few blocks.
 * This leaks data and opens the possibility of some attacks.
 *
 * This class can decrypt the crypto built by [AESEncrypter].
 * To do this, you should use the same 16-byte secret key as the key in [AESEncrypter] to construct
 * the decrypter.
 *
 * @param secretKey the 16-byte secret key using in decryption.
 * @param iv the 16-byte initial vector using in decryption.
 * @throws InvalidSpecificationException if the platform does not support the transformation.
 * @throws InvalidKeyException if the secret key is not 16 bytes.
 */
class FixedIVAESCBCDecrypter
@Throws(InvalidSpecificationException::class, InvalidKeyException::class)
constructor(secretKey: ByteArray, iv: ByteArray): Decrypter<ByteArray> {
    private val decrypter: Cipher

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
            decrypter = Cipher.getInstance("AES/CBC/PKCS5Padding")
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
        decrypter.init(Cipher.DECRYPT_MODE, secretKeySpec, ivSpec)
    }

    /**
     * Convert the given encrypted text to origin text.
     *
     * Note that the length of input text should be a multiple of 16, and the text must be
     * encrypted by the AES algorithm.
     *
     * @param text any encrypted text (byte array).
     * @returns origin text (byte array).
     * @throws IllegalBlockSizeException if the given text is not a multiple of 16.
     * @throws DecryptionException if the given text is not in a correct form.
     */
    @Throws(
        IllegalBlockSizeException::class,
        DecryptionException::class
    )
    override fun decrypt(text: ByteArray): ByteArray {
        // Decrypt encrypted text
        return try {
            decrypter.doFinal(text)
        } catch (e: javax.crypto.IllegalBlockSizeException) {
            // If the crypto is not a multiple of 16
            throw IllegalBlockSizeException()
        } catch (e: javax.crypto.BadPaddingException) {
            // If the crypto is not in a correct form
            throw DecryptionException()
        }
    }
}
