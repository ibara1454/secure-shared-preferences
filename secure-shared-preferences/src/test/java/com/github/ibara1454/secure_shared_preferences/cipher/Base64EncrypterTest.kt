package com.github.ibara1454.secure_shared_preferences.cipher

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class Base64EncrypterTest {
    @Test
    fun test_encrypt_convert_cipher_text_simple_text() {
        val encrypter = Base64Encrypter()

        val input = "hello world".toByteArray()

        val actual = encrypter.encrypt(input)

        assertThat(actual).isEqualTo("aGVsbG8gd29ybGQ=".toByteArray())
    }
}
