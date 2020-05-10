package com.github.ibara1454.secure_shared_preferences.secret

import java.security.SecureRandom

/**
 * The generator for generating secret key.
 */
class SecretGenerator {
    /**
     * Generates a random secret key.
     * @return secret key.
     */
    fun generate(): SecretKey {
        val secret = ByteArray(SECRET_LENGTH)
        val random = SecureRandom()
        random.nextBytes(secret)
        return secret
    }

    companion object {
        // TODO: remove the hard coded key length
        // Hard coded the key length of AES algorithm
        private const val SECRET_LENGTH = 16
    }
}
