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
        val input = byteArrayOfInts(
            0x13, 0x77, 0x1A, 0x22, 0xDB, 0x9B, 0xB4, 0x49,
            0x2E, 0x65, 0x24, 0x8E, 0x7C, 0x2A, 0xE0, 0x14,
            0x9F, 0x6A, 0x85, 0x6E, 0x3D, 0x60, 0xD7, 0xDB,
            0xE4, 0x2D, 0x71, 0x9E, 0xA8, 0x6F, 0x04, 0xAB
        )
        val actual = decrypter.decrypt(input)

        assertThat(actual).isEqualTo("".toByteArray())
    }

    @Test
    fun test_decrypt_convert_cipher_text_simple_text() {
        val key = "1234567890123456".toByteArray() // 128-bit string
        val encrypter = AESDecrypter(key)
        val input = byteArrayOfInts(
            0xF7, 0xFE, 0x4B, 0x9C, 0x4E, 0x32, 0x75, 0x7E,
            0x6C, 0x4F, 0xFF, 0x98, 0x1C, 0xF4, 0x53, 0x07,
            0x3E, 0x24, 0xC8, 0x69, 0xC9, 0x55, 0x4A, 0x6C,
            0xDF, 0x3E, 0xD1, 0x2C, 0x15, 0x7C, 0x50, 0xDC
        )
        val actual = encrypter.decrypt(input)

        assertThat(actual).isEqualTo("hello world".toByteArray())
    }
}
