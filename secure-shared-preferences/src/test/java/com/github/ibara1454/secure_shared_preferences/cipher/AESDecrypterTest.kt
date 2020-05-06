package com.github.ibara1454.secure_shared_preferences.cipher

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class AESDecrypterTest {
    private fun byteArrayOfInts(vararg ints: Int) = ByteArray(ints.size) { pos -> ints[pos].toByte() }

    @Test(expected = IllegalArgumentException::class)
    fun test_constructor_throws_exception_with_non_128_bits_key() {
        val key = "".toByteArray()
        AESDecrypter(key)
    }

    @Test
    fun test_constructor_initialized_done_with_128_bits_key() {
        val key = "1234567890123456".toByteArray() // 128-bit string
        AESDecrypter(key)
    }

    @Test
    fun test_decrypt_convert_cipher_text_to_empty_text() {
        val key = "1234567890123456".toByteArray() // 128-bit string
        val decrypter = AESDecrypter(key)
        val input = byteArrayOfInts(0x7D, 0x56, 0x86, 0x60, 0x76, 0xCF, 0x36, 0xF2, 0xA8, 0x12, 0x13, 0x1F, 0xDF, 0x3F, 0xF7, 0x42)

        val actual = decrypter.decrypt(input)

        assertThat(actual).isEqualTo("".toByteArray())
    }

    @Test
    fun test_decrypt_convert_cipher_text_simple_text() {
        val key = "1234567890123456".toByteArray() // 128-bit string
        val encrypter = AESDecrypter(key)
        val input = byteArrayOfInts(0xF7, 0xFE, 0x4B, 0x9C, 0x4E, 0x32, 0x75, 0x7E, 0x6C, 0x4F, 0xFF, 0x98, 0x1C, 0xF4, 0x53, 0x07, 0x3E, 0x24, 0xC8, 0x69, 0xC9, 0x55, 0x4A, 0x6C, 0xDF, 0x3E, 0xD1, 0x2C, 0x15, 0x7C, 0x50, 0xDC)
        val actual = encrypter.decrypt(input)

        assertThat(actual).isEqualTo("hello world".toByteArray())
    }
}
