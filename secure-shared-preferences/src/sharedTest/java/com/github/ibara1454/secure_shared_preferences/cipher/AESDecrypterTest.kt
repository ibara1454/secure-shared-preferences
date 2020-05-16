package com.github.ibara1454.secure_shared_preferences.cipher

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class AESDecrypterTest {
    private fun byteArrayOfInts(vararg ints: Int) =
        ByteArray(ints.size) { pos -> ints[pos].toByte() }

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

    @Test
    fun test_decrypt_convert_long_cipher_to_text() {
        val key = "1234567890123456".toByteArray() // 128-bit string
        val decrypter = AESDecrypter(key)
        val input = byteArrayOfInts(
            0xA1, 0x95, 0xFD, 0xC5, 0xDD, 0x56, 0xDB, 0xDE,
            0xED, 0x02, 0x8B, 0xF8, 0x92, 0xBD, 0xD7, 0xC5,
            0x36, 0x3E, 0x12, 0x04, 0x99, 0x48, 0x76, 0x89,
            0x72, 0xE2, 0x49, 0xB9, 0xA0, 0x43, 0x8A, 0x2B,
            0x2A, 0x8B, 0x22, 0x1C, 0x6D, 0x5A, 0x13, 0xBF,
            0x7B, 0xEC, 0xD1, 0x27, 0x20, 0xF9, 0xAA, 0x5A
        )

        val actual = decrypter.decrypt(input)
        assertThat(actual).isEqualTo("1234567890123456".toByteArray())
    }
}
