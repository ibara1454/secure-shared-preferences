package com.github.ibara1454.secure_shared_preferences.cipher

import org.junit.Test

import com.google.common.truth.Truth.assertThat

class AESEncrypterTest {
    private fun byteArrayOfInts(vararg ints: Int) = ByteArray(ints.size) { pos -> ints[pos].toByte() }

    @Test(expected = IllegalArgumentException::class)
    fun test_constructor_throws_exception_with_non_128_bits_key() {
        val key = "".toByteArray()
        AESEncrypter(key)
    }

    @Test
    fun test_constructor_initialized_done_with_128_bits_key() {
        val key = "1234567890123456".toByteArray() // 128-bit string
        AESEncrypter(key)
    }

    @Test
    fun test_encrypt_convert_empty_text_to_cipher_text() {
        val key = "1234567890123456".toByteArray() // 128-bit string
        val encrypter = AESEncrypter(key)
        val input = "".toByteArray()

        val actual = encrypter.encrypt(input)

        assertThat(actual).isEqualTo(
            byteArrayOfInts(0x7D, 0x56, 0x86, 0x60, 0x76, 0xCF, 0x36, 0xF2, 0xA8, 0x12, 0x13, 0x1F, 0xDF, 0x3F, 0xF7, 0x42)
        )
    }

    @Test
    fun test_encrypt_convert_simple_text_to_cipher_text() {
        val key = "1234567890123456".toByteArray() // 128-bit string
        val encrypter = AESEncrypter(key)
        val input = "hello world".toByteArray() // 0x68, 0x65, 0x6C, 0x6C, 0x6F, 0x20, 0x77, 0x6F, 0x72, 0x6C, 0x64

        val actual = encrypter.encrypt(input)

        assertThat(actual).isEqualTo(
            byteArrayOfInts(0x0C, 0x33, 0x3C, 0xD8, 0x6A, 0x0C, 0xA1, 0x40, 0xB8, 0x79, 0x63, 0x8B, 0x89, 0x31, 0x29, 0xE7)
        )
    }
}
