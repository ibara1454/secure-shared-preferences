package com.github.ibara1454.secure_shared_preferences.cipher

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class StringDecrypterTest {

    @Test
    fun test_primary_constructor_finished_without_exception() {
        val byteArrayDecrypter = object : Decrypter<ByteArray> {
            override fun decrypt(text: ByteArray): ByteArray = text
        }
        StringDecrypter(byteArrayDecrypter)
    }

    @Test
    fun test_secondary_constructor_finished_without_exception() {
        StringDecrypter { bytes -> bytes }
    }

    @Test
    fun test_decrypt_convert_cipher_text_to_plain_text() {
        val decrypter = StringDecrypter { bytes -> bytes }

        val input = "hello world"

        val actual = decrypter.decrypt(input)

        assertThat(actual).isEqualTo(input)
    }
}
