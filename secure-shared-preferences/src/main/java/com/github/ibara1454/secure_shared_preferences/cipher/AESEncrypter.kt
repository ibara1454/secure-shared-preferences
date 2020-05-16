package com.github.ibara1454.secure_shared_preferences.cipher

import com.github.ibara1454.secure_shared_preferences.exception.InvalidKeyException
import com.github.ibara1454.secure_shared_preferences.exception.InvalidSpecificationException
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

/**
 * The encrypter using AES algorithm.
 *
 * The class provides a [encrypt] method to encrypt any text.
 * To decrypt the encrypted text, you could use the [AESDecrypter] class.
 *
 * @param secretKey the 16-byte secret key using in encryption.
 * @throws InvalidKeyException if the secret key is not 16 bytes.
 * @throws InvalidSpecificationException if the platform does not support the transformation.
 */
internal class AESEncrypter
@Throws(InvalidSpecificationException::class, InvalidKeyException::class)
constructor(secretKey: ByteArray) : Encrypter<ByteArray> {
    private val encrypter: Cipher
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
        secretKeySpec = SecretKeySpec(secretKey, "AES")
    }

    /**
     * Encrypt the given plain text.
     *
     * Note that this method will return a byte array which length is a multiple of 16. The byte
     * array contains the following two parts: the initial vector part and the crypto part.
     *
     *         | Initial Vector (IV) part |             Crypto part            |
     *         | < -----  16 bytes -----> | < ----- multiple of 16 bytes ----> |
     *
     * The initial vector in each encryption would not be the same value. It will be re-generated
     * in each encryption.
     *
     * The length of crypto part is dependent on the input text. If the length of input text is
     * between 0 and 15, then we will get 16 for crypto. And if the length of input text is
     * between 16 and 31, then we will get 32 for crypto and so on.
     *
     * @param text any text.
     * @returns encrypted text.
     */
    override fun encrypt(text: ByteArray): ByteArray {
        // Initialize the encrypter without initial vector - this will make initial vector be
        //  auto-generated. We will use new initial vector for different encryption.
        encrypter.init(Cipher.ENCRYPT_MODE, secretKeySpec)
        val iv = encrypter.iv
        // Encryption should never throw exceptions
        val crypto = encrypter.doFinal(text)
        // Combine iv and crypto
        return iv + crypto
    }
}
