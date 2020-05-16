package com.github.ibara1454.secure_shared_preferences.cipher

import com.github.ibara1454.secure_shared_preferences.exception.DecryptionException
import com.github.ibara1454.secure_shared_preferences.exception.IllegalBlockSizeException
import com.github.ibara1454.secure_shared_preferences.exception.InvalidIVException
import com.github.ibara1454.secure_shared_preferences.exception.InvalidKeyException
import com.github.ibara1454.secure_shared_preferences.exception.InvalidSpecificationException
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

/**
 * The decrypter using AES algorithm.
 *
 * This class can decrypt the crypto built by [AESEncrypter].
 * To do this, you should use the same 16-byte secret key as the key in [AESEncrypter] to construct
 * the decrypter.
 *
 * @param secretKey the 16-byte secret key using in decryption.
 * @throws InvalidSpecificationException if the platform does not support the transformation.
 * @throws InvalidKeyException if the secret key is not 16 bytes.
 */
internal class AESDecrypter
@Throws(InvalidSpecificationException::class, InvalidKeyException::class)
constructor(secretKey: ByteArray) : Decrypter<ByteArray> {
    private val decrypter: Cipher
    // The 16-byte length secret key
    private val secretKeySpec: SecretKeySpec

    init {
        // The key length should be 16 bytes (128 bits) when using AES algorithm
        if (secretKey.size != 16) {
            throw InvalidKeyException()
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
        secretKeySpec = SecretKeySpec(secretKey, "AES")
    }

    /**
     * Convert the given encrypted text to origin text.
     *
     * Note that the length of input text should be a multiple of 16, and the text must be
     * encrypted by the AES algorithm. The text should be formed by two parts: the initial vector
     * part and the crypto part. That is,
     *
     *         | Initial Vector (IV) part |             Crypto part            |
     *         | < -----  16 bytes -----> | < ----- multiple of 16 bytes ----> |
     *
     * @param text any encrypted text (byte array).
     * @returns origin text (byte array).
     * @throws InvalidIVException if given text is under 16 bytes.
     * @throws IllegalBlockSizeException if the given text is not a multiple of 16.
     * @throws DecryptionException if the given text is not in a correct form.
     */
    @Throws(
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
        } catch (e: java.security.InvalidAlgorithmParameterException) {
            // If iv is not 16-byte long
            throw InvalidIVException()
        }

        // Decrypt encrypted text
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
