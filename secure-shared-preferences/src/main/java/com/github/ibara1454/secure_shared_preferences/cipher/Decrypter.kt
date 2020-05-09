package com.github.ibara1454.secure_shared_preferences.cipher

/**
 * The decrypter provides the method [decrypt] to convert an encrypted text to origin text.
 */
interface Decrypter<T> {
    /**
     * Convert the given encrypted text to origin text.
     * @param text any encrypted text.
     * @returns origin text.
     */
    fun decrypt(text: T): T
}
