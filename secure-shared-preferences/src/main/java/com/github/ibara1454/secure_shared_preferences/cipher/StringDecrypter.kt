package com.github.ibara1454.secure_shared_preferences.cipher

/**
 * The decrypter is a adapter which accept a `byte-array-to-byte-array` decrypter and convert it
 * to a `string-to-string` decrypter.
 */
internal class StringDecrypter(private val decrypter: Decrypter<ByteArray>) : Decrypter<String> {

    /**
     * Constructor for converting given function to [StringDecrypter] (since Kotlin 1.3 doesn't
     * support SAM conversion on Kotlin).
     * @param decrypt any decryption function which transforms encrypted data to plain data.
     */
    constructor(decrypt: (ByteArray) -> ByteArray) : this(object : Decrypter<ByteArray> {
        override fun decrypt(text: ByteArray): ByteArray = decrypt(text)
    })

    private val charset = Charsets.UTF_8

    /**
     * Convert the given encrypted [text] to original text.
     * @param text any encrypted string.
     * @return original text.
     */
    override fun decrypt(text: String): String {
        val input = text.toByteArray(charset)
        // `toString` will not throw any exception and always replaces malformed-input and
        //  unmappable-character sequences with this charset's default replacement string.
        // But `decrypter::decrypt` may throw exceptions. It depends on the given wrapped decrypter.
        return decrypter.decrypt(input).toString(charset)
    }
}
