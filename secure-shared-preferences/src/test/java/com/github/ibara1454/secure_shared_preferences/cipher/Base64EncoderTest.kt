package com.github.ibara1454.secure_shared_preferences.cipher

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class Base64EncoderTest {
    @Test
    fun test_encode_convert_cipher_text_simple_text() {
        val encoder = Base64Encoder()

        val input = "hello world".toByteArray()

        val actual = encoder.encode(input)

        assertThat(actual).isEqualTo("aGVsbG8gd29ybGQ=".toByteArray())
    }
}
