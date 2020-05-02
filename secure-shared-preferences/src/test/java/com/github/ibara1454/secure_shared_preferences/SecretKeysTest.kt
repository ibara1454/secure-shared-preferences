package com.github.ibara1454.secure_shared_preferences

import android.content.SharedPreferences
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.ibara1454.secure_shared_preferences.cipher.*
import com.google.common.truth.Truth.assertThat
import io.mockk.*
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class SecretKeysTest {
    // Context of the app under test.
    private val appContext = InstrumentationRegistry.getInstrumentation().targetContext

    @Test
    fun test_getOrCreate_does_not_generate_new_key_if_key_already_exists() {
        mockkObject(SecretKeys)
        val key = "secret key".toByteArray()
        every { SecretKeys.getConfig(any()) getProperty "secretKey" } returns key
        every { SecretKeys.getSecretGenerator() } returns mockk()

        val actual = SecretKeys.getOrCreate(appContext)

        verify {
            SecretKeys.getConfig(any()) getProperty "secretKey"
            SecretKeys.getSecretGenerator() wasNot called
        }
        assertThat(actual).isEqualTo(key)
    }

    @Test
    fun test_getOrCreate_generates_new_key_if_key_not_exists() {
        mockkObject(SecretKeys)
        val key = null
        val newKey = "new secret key".toByteArray()
        every { SecretKeys.getConfig(any()) getProperty "secretKey" } returns key
        every { SecretKeys.getConfig(any()) setProperty "secretKey" value any<ByteArray>() } just Runs
        every { SecretKeys.getSecretGenerator().generate() } returns newKey

        val actual = SecretKeys.getOrCreate(appContext)

        verify(exactly = 1) {
            SecretKeys.getConfig(any()) getProperty "secretKey"
            SecretKeys.getSecretGenerator().generate()
            SecretKeys.getConfig(any()) setProperty "secretKey" value newKey
        }
        assertThat(actual).isEqualTo(newKey)
    }

    @Test
    fun test_getOrCreate_crashes_before_new_key_returns_if_saving_secret_key_failed() {
        mockkObject(SecretKeys)
        val key = null
        val newKey = "new secret key".toByteArray()
        val exception = IOException()
        every { SecretKeys.getConfig(any()) getProperty "secretKey" } returns key
        every { SecretKeys.getConfig(any()) setProperty "secretKey" value any<ByteArray>() } throws exception
        every { SecretKeys.getSecretGenerator().generate() } returns newKey

        try {
            val actual = SecretKeys.getOrCreate(appContext)
            assertThat(actual).isNull() // This line should not be executed
        } catch (e: Exception) {
            assertThat(e).isInstanceOf(IOException::class.java)
        }

        verify(exactly = 1) {
            SecretKeys.getConfig(any()) getProperty "secretKey"
            SecretKeys.getSecretGenerator().generate()
            SecretKeys.getConfig(any()) setProperty "secretKey" value newKey
        }
    }
}

@RunWith(AndroidJUnit4::class)
class SecretKeysConfigTest {
    @Test
    fun test_secretKey_get_returns_null_if_key_is_null() {
        val preferences = mockk<SharedPreferences>()
        every { preferences.getString(any(), any()) } returns null

        val encrypter = mockk<Encrypter<SecretKey>>()

        val decrypter = mockk<Decrypter<SecretKey>>()

        val config = SecretKeys.SecretKeysConfig(preferences, encrypter, decrypter)
        val actual = config.secretKey

        assertThat(actual).isNull()
    }

    @Test
    fun test_secretKey_get_returns_null_if_key_is_empty() {
        val preferences = mockk<SharedPreferences>()
        every { preferences.getString(any(), any()) } returns ""

        val encrypter = mockk<Encrypter<SecretKey>>()

        val decrypter = mockk<Decrypter<SecretKey>>()

        val config = SecretKeys.SecretKeysConfig(preferences, encrypter, decrypter)
        val actual = config.secretKey

        assertThat(actual).isNull()
    }

    @Test
    fun test_secretKey_get_returns_byte_array_if_key_exist() {
        val preferences = mockk<SharedPreferences>()
        val key = "dummy key".toByteArray()
        every { preferences.getString(any(), any()) } returns (key + 0x0.toByte()).toString(Charsets.UTF_8)

        val encrypter = mockk<Encrypter<SecretKey>>()

        val decrypter = mockk<Decrypter<SecretKey>>()
        every { decrypter.decrypt(any()) } answers {
            val bytes = firstArg<ByteArray>()
            bytes.take(bytes.size - 1).toByteArray()
        }

        val config = SecretKeys.SecretKeysConfig(preferences, encrypter, decrypter)
        val actual = config.secretKey

        assertThat(actual).isEqualTo(key)
    }

    @Test
    fun test_secretKey_set_just_run_if_commit_succeeded() {
        val key = "dummy key".toByteArray()
        val preferences = mockk<SharedPreferences>()
        val editor = mockk<SharedPreferences.Editor>()
        every { preferences.edit() } returns editor
        every { editor.putString(any(), any()) } returns editor
        every { editor.commit() } returns true

        val encrypter = mockk<Encrypter<SecretKey>>()
        every { encrypter.encrypt(any()) } answers { firstArg<ByteArray>() + 0x0.toByte() }

        val decrypter = mockk<Decrypter<SecretKey>>()

        val config = SecretKeys.SecretKeysConfig(preferences, encrypter, decrypter)
        config.secretKey = key

        verify {
            editor.putString(any(), (key + 0x0.toByte()).toString(Charsets.UTF_8))
            editor.commit()
        }
    }

    @Test
    fun test_secretKey_set_throws_exception_if_commit_failed() {
        val key = "dummy key".toByteArray()
        val preferences = mockk<SharedPreferences>()
        val editor = mockk<SharedPreferences.Editor>()
        every { preferences.edit() } returns editor
        every { editor.putString(any(), any()) } returns editor
        every { editor.commit() } returns false

        val encrypter = mockk<Encrypter<SecretKey>>()
        every { encrypter.encrypt(any()) } answers { firstArg<ByteArray>() + 0x0.toByte() }

        val decrypter = mockk<Decrypter<SecretKey>>()

        val config = SecretKeys.SecretKeysConfig(preferences, encrypter, decrypter)

        try {
            config.secretKey = key
            assertThat(false).isTrue() // This line should not be executed
        } catch (e: Exception) {
            assertThat(e).isInstanceOf(IOException::class.java)
        }

        verify {
            editor.putString(any(), (key + 0x0.toByte()).toString(Charsets.UTF_8))
            editor.commit()
        }
    }
}
