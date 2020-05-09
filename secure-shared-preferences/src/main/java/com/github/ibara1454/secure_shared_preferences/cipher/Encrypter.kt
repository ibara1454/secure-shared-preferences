package com.github.ibara1454.secure_shared_preferences.cipher

/**
 * The encrypter provides the method [encrypt] to encrypt any plain text.
 */
interface Encrypter<T> {
    /**
     * Encrypt the given text.
     * @param text any text.
     * @returns encrypted text.
     */
    fun encrypt(text: T): T
}
