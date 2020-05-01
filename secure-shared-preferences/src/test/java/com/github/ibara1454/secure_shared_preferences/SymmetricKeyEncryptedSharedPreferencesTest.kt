package com.github.ibara1454.secure_shared_preferences

import android.content.Context
import android.content.SharedPreferences
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.ibara1454.secure_shared_preferences.SymmetricKeyEncryptedSharedPreferencesFactory.SymmetricKeyEncryptedSharedPreferencesConfig
import com.google.common.truth.Truth.assertThat
import io.mockk.*
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class SymmetricKeyEncryptedSharedPreferencesFactoryTest {
    // Context of the app under test.
    private val appContext = InstrumentationRegistry.getInstrumentation().targetContext

    @Test
    fun test_create_returns_encrypted_shared_preferences_with_existed_key_if_key_exist() {
        mockkConstructor(SymmetricKeyEncryptedSharedPreferencesConfig::class)
        every { anyConstructed<SymmetricKeyEncryptedSharedPreferencesConfig>() getProperty "secretKey" } returns "dummy secret key".toByteArray()

        val factory = spyk(SymmetricKeyEncryptedSharedPreferencesFactory())
        every { factory.getSecretGenerator() } returns mockk()
        every { factory.getSymmetricKeyEncryptedSharedPreferences(any(), any(), any()) } returns mockk()

        factory.create("name", Context.MODE_PRIVATE, appContext)

        verify {
            factory.getSymmetricKeyEncryptedSharedPreferences(
         "41dd1ef45721398d6633e907363d91bc266a476f4cb4bef61f8c8b669b1de982",
                byteArrayOf(71, -6, -39, 122, -19, -86, 90, 14, -123, 86, -65, -35, -56, -4, -51, -95),
                appContext
            )
            anyConstructed<SymmetricKeyEncryptedSharedPreferencesConfig>() getProperty "secretKey"
            factory.getSecretGenerator() wasNot called
            factory.getSymmetricKeyEncryptedSharedPreferences(
                "name",
                "dummy secret key".toByteArray(),
                appContext
            )
        }
    }

    @Test
    fun test_create_returns_encrypted_shared_preferences_with_new_key_if_key_is_not_exist() {
        mockkConstructor(SymmetricKeyEncryptedSharedPreferencesConfig::class)
        every { anyConstructed<SymmetricKeyEncryptedSharedPreferencesConfig>() getProperty "secretKey" } returns null
        every { anyConstructed<SymmetricKeyEncryptedSharedPreferencesConfig>() setProperty "secretKey" value any<ByteArray>() } just Runs

        val factory = spyk(SymmetricKeyEncryptedSharedPreferencesFactory())
        every { factory.getSecretGenerator().generate() } returns "new secret key".toByteArray()
        every { factory.getSymmetricKeyEncryptedSharedPreferences(any(), any(), any()) } returns mockk()

        factory.create("name", Context.MODE_PRIVATE, appContext)

        verify {
            factory.getSymmetricKeyEncryptedSharedPreferences(
                "41dd1ef45721398d6633e907363d91bc266a476f4cb4bef61f8c8b669b1de982",
                byteArrayOf(71, -6, -39, 122, -19, -86, 90, 14, -123, 86, -65, -35, -56, -4, -51, -95),
                appContext
            )
            anyConstructed<SymmetricKeyEncryptedSharedPreferencesConfig>() getProperty "secretKey"
            factory.getSecretGenerator().generate()
            anyConstructed<SymmetricKeyEncryptedSharedPreferencesConfig>() setProperty "secretKey" value "new secret key".toByteArray()
            factory.getSymmetricKeyEncryptedSharedPreferences(
                "name",
                "new secret key".toByteArray(),
                appContext
            )
        }
    }

    @Test
    fun test_create_crashed_before_return_if_saving_secret_key_failed() {
        mockkConstructor(SymmetricKeyEncryptedSharedPreferencesConfig::class)
        every { anyConstructed<SymmetricKeyEncryptedSharedPreferencesConfig>() getProperty "secretKey" } returns null
        every { anyConstructed<SymmetricKeyEncryptedSharedPreferencesConfig>() setProperty "secretKey" value any<ByteArray>() } throws IOException()

        val factory = spyk(SymmetricKeyEncryptedSharedPreferencesFactory())
        every { factory.getSecretGenerator().generate() } returns "new secret key".toByteArray()
        every { factory.getSymmetricKeyEncryptedSharedPreferences(any(), any(), any()) } returns mockk()

        try {
            val actual = factory.create("name", Context.MODE_PRIVATE, appContext)
            assertThat(actual).isNull() // This line should not be executed
        } catch (e: Exception) {
            assertThat(e).isInstanceOf(IOException::class.java)
        }

        verify {
            factory.getSymmetricKeyEncryptedSharedPreferences(
                "41dd1ef45721398d6633e907363d91bc266a476f4cb4bef61f8c8b669b1de982",
                byteArrayOf(71, -6, -39, 122, -19, -86, 90, 14, -123, 86, -65, -35, -56, -4, -51, -95),
                appContext
            )
            anyConstructed<SymmetricKeyEncryptedSharedPreferencesConfig>() getProperty "secretKey"
            factory.getSecretGenerator().generate()
            anyConstructed<SymmetricKeyEncryptedSharedPreferencesConfig>() setProperty "secretKey" value "new secret key".toByteArray()
        }
    }

    class SymmetricKeyEncryptedSharedPreferencesConfigTest {
        @Test
        fun test_secretKey_get_returns_null_if_key_is_null() {
            val preferences = mockk<SharedPreferences>()
            every { preferences.getString(any(), any()) } returns null

            val config = SymmetricKeyEncryptedSharedPreferencesConfig(preferences)
            val actual = config.secretKey

            assertThat(actual).isNull()
        }

        @Test
        fun test_secretKey_get_returns_null_if_key_is_empty() {
            val preferences = mockk<SharedPreferences>()
            every { preferences.getString(any(), any()) } returns ""

            val config = SymmetricKeyEncryptedSharedPreferencesConfig(preferences)
            val actual = config.secretKey

            assertThat(actual).isNull()
        }

        @Test
        fun test_secretKey_get_returns_byte_array_if_key_exist() {
            val preferences = mockk<SharedPreferences>()
            every { preferences.getString(any(), any()) } returns "dummy key"

            val config = SymmetricKeyEncryptedSharedPreferencesConfig(preferences)
            val actual = config.secretKey

            assertThat(actual).isNotNull()
        }

        @Test
        fun test_secretKey_set_just_run_if_commit_succeeded() {
            val preferences = mockk<SharedPreferences>()
            val editor = mockk<SharedPreferences.Editor>()
            every { preferences.edit() } returns editor
            every { editor.putString(any(), any()) } returns editor
            every { editor.commit() } returns true

            val config = SymmetricKeyEncryptedSharedPreferencesConfig(preferences)
            config.secretKey = "dummy key".toByteArray()

            verify { editor.commit() }
        }

        @Test
        fun test_secretKey_set_throws_exception_if_commit_failed() {
            val preferences = mockk<SharedPreferences>()
            val editor = mockk<SharedPreferences.Editor>()
            every { preferences.edit() } returns editor
            every { editor.putString(any(), any()) } returns editor
            every { editor.commit() } returns false

            val config = SymmetricKeyEncryptedSharedPreferencesConfig(preferences)

            try {
                config.secretKey = "dummy key".toByteArray()
                assertThat(false).isTrue() // This line should not be executed
            } catch (e: Exception) {
                assertThat(e).isInstanceOf(IOException::class.java)
            }

            verify { editor.commit() }
        }
    }
}

class SymmetricKeyEncryptedSharedPreferencesTest {
    // No tests needed
}
