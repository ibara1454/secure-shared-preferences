package com.github.ibara1454.secure_shared_preferences.cipher

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class StringEncrypterTest {

    @Test
    fun test_primary_constructor_finished_without_exception() {
        val byteArrayEncrypter = object : Encrypter<ByteArray> {
            override fun encrypt(text: ByteArray): ByteArray = text
        }
        StringEncrypter(byteArrayEncrypter)
    }

    @Test
    fun test_secondary_constructor_finished_without_exception() {
        StringEncrypter { bytes -> bytes }
    }

    @Test
    fun test_decrypt_convert_cipher_text_to_plain_text() {
        val encrypter = StringEncrypter { bytes -> bytes }

        val input = "hello world"

        val actual = encrypter.encrypt(input)

        assertThat(actual).isEqualTo(input)
    }
}
