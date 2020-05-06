package com.github.ibara1454.secure_shared_preferences

import com.github.ibara1454.secure_shared_preferences.shared_preferences.SecretKey
import java.security.SecureRandom

class SecretGenerator {
    fun generate(): SecretKey {
        val secret = ByteArray(SECRET_LENGTH)
        val random = SecureRandom()
        random.nextBytes(secret)
        return secret
    }

    companion object {
        private const val SECRET_LENGTH = 16
    }
}
