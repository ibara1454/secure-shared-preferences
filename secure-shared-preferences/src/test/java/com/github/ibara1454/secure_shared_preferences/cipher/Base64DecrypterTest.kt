package com.github.ibara1454.secure_shared_preferences.cipher

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class Base64DecrypterTest {
    @Test
    fun test_decrypt_convert_simple_text_to_cipher_text() {
        val decrypter = Base64Decrypter()

        val input = "aGVsbG8gd29ybGQ=".toByteArray() // "hello world"

        val actual = decrypter.decrypt(input)

        assertThat(actual).isEqualTo("hello world".toByteArray())
    }
}
