package com.github.ibara1454.secure_shared_preferences.cipher

import org.junit.Test

import com.google.common.truth.Truth.assertThat

class AESEncrypterTest {

    @Test(expected = IllegalArgumentException::class)
    fun test_constructor_throws_exception_with_non_128_bits_key() {
        val key = "".toByteArray()
        AESEncrypter(key)
    }

    @Test
    fun test_constructor_initialized_done_with_128_bits_key() {
        val key = "1234567890123456".toByteArray() // 16-byte (128-bit) string
        AESEncrypter(key)
    }

    @Test
    fun test_encrypt_convert_empty_text_to_cipher_text() {
        val key = "1234567890123456".toByteArray() // 16-byte (128-bit) string
        val encrypter = AESEncrypter(key)
        val input = "".toByteArray()

        val actual = encrypter.encrypt(input)

        assertThat(actual.size).isEqualTo(16 + 16) // 16-byte iv + 16-byte crypto
    }

    @Test
    fun test_encrypt_convert_simple_text_to_cipher_text() {
        val key = "1234567890123456".toByteArray() // 128-bit string
        val encrypter = AESEncrypter(key)
        val input = "hello world".toByteArray() // 0x68, 0x65, 0x6C, 0x6C, 0x6F, 0x20, 0x77, 0x6F, 0x72, 0x6C, 0x64

        val actual = encrypter.encrypt(input)

        assertThat(actual.size).isEqualTo(16 + 16) // 16-byte iv + 16-byte crypto
    }

    @Test
    fun test_encrypt_convert_long_text_to_cipher_text() {
        val key = "1234567890123456".toByteArray() // 128-bit string
        val encrypter = AESEncrypter(key)
        val input = "1234567890123456".toByteArray() // 16 bytes

        val actual = encrypter.encrypt(input)
        // Under PKCS#5 or PKCS#7 padding, the crypto of 16~31 bytes text will be 32 bytes
        assertThat(actual.size).isEqualTo(16 + 32) // 16-byte iv + 32-byte crypto
    }
}
