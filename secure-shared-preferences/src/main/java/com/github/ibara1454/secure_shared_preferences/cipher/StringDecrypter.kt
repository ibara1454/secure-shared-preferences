package com.github.ibara1454.secure_shared_preferences.cipher

internal class StringDecrypter(private val decrypter: Decrypter<ByteArray>) : Decrypter<String> {

    constructor(decrypt: (ByteArray) -> ByteArray) : this(object : Decrypter<ByteArray> {
        override fun decrypt(text: ByteArray): ByteArray = decrypt(text)
    })

    private val charset = Charsets.UTF_8

    override fun decrypt(text: String): String {
        val input = text.toByteArray(charset)
        return decrypter.decrypt(input).toString(charset)
    }
}
