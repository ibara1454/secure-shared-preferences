package com.github.ibara1454.secure_shared_preferences.shared_preferences

/**
 * This class indicates how we save secret key.
 *
 * NONE: do not use encrypted SharedPreferences and do not save the key.
 * SAFE: encrypt the secret key and save it into SharedPreferences.
 * KEYSTORE: save the secret key into KeyStore.
 */
internal enum class EncryptType {
    NONE { override fun downgrade() = NONE },
    SAFE { override fun downgrade() = NONE },
    KEYSTORE { override fun downgrade() = SAFE };

    /**
     * Returns the lower encryption type.
     * @return lower encryption type.
     */
    abstract fun downgrade(): EncryptType
}
