package com.github.ibara1454.secure_shared_preferences.cipher

/**
 * The encrypter is a adapter which accept a `byte-array-to-byte-array` encrypter and convert it
 * to a `string-to-string` encrypter.
 */
internal class StringEncrypter(private val encrypter: Encrypter<ByteArray>) : Encrypter<String> {

    /**
     * Constructor for converting given function to [StringEncrypter] (since Kotlin 1.3 doesn't
     * support SAM conversion on Kotlin).
     * @param encrypt any encryption function which transforms plain data to encrypted data.
     */
    constructor(encrypt: (ByteArray) -> ByteArray) : this(object : Encrypter<ByteArray> {
        override fun encrypt(text: ByteArray): ByteArray = encrypt(text)
    })

    private val charset = Charsets.UTF_8

    /**
     * Convert the given [text] to encrypted text.
     * @param text any string.
     * @return encrypted text.
     */
    override fun encrypt(text: String): String {
        val input = text.toByteArray(charset)
        // `toString` will not throw any exception and always replaces malformed-input and
        //  unmappable-character sequences with this charset's default replacement string.
        // But `encrypter::encrypt` may throw exceptions. It depends on the given wrapped encrypter.
        return encrypter.encrypt(input).toString(charset)
    }
}
