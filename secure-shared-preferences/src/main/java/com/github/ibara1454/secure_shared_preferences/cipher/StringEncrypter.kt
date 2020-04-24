package com.github.ibara1454.secure_shared_preferences.cipher

internal class StringEncrypter(private val encrypter: Encrypter<ByteArray>) : Encrypter<String> {

    constructor(encrypt: (ByteArray) -> ByteArray) : this(object : Encrypter<ByteArray> {
        override fun encrypt(text: ByteArray): ByteArray = encrypt(text)
    })

    private val charset = Charsets.UTF_8

    override fun encrypt(text: String): String {
        val input = text.toByteArray(charset)
        return encrypter.encrypt(input).toString(charset)
    }
}
