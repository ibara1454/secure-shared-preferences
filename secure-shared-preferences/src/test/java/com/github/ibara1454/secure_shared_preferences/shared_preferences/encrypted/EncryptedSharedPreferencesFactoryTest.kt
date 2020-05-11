package com.github.ibara1454.secure_shared_preferences.shared_preferences.encrypted
import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.ibara1454.secure_shared_preferences.shared_preferences.keystore.KeystoreEncryptedSharedPreferencesFactory
import com.github.ibara1454.secure_shared_preferences.shared_preferences.safe.SafeSharedPreferences
import com.github.ibara1454.secure_shared_preferences.shared_preferences.safe.SafeSharedPreferencesFactory
import com.google.common.truth.Truth.assertThat
import io.mockk.*
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import java.lang.Exception

@RunWith(AndroidJUnit4::class)
class SecureSharedPreferencesFactoryTest {
    // Context of the app under test.
    private val appContext = InstrumentationRegistry.getInstrumentation().targetContext

    @Test
    fun test_create_returns_normal_shared_preferences_if_type_equals_to_NORMAL() {
        mockkConstructor(SafeSharedPreferencesFactory::class)
        every { anyConstructed<SafeSharedPreferencesFactory>().create(any(), any()) } returns mockk<SafeSharedPreferences>()

        mockkConstructor(KeystoreEncryptedSharedPreferencesFactory::class)
        every { anyConstructed<KeystoreEncryptedSharedPreferencesFactory>().create(any(), any()) } returns mockk<EncryptedSharedPreferences>()

        val config = mockk<EncryptedSharedPreferencesFactory.EncryptedSharedPreferencesConfig>()

        val name = "name"
        val mode = Context.MODE_PRIVATE
        val type =
            EncryptType.NONE
        val actual = EncryptedSharedPreferencesFactory(
            appContext,
            config
        ).create(name, mode, type)

        // Is instance of SharedPreferences
        assertThat(actual).isNotInstanceOf(SafeSharedPreferences::class.java)
        assertThat(actual).isNotInstanceOf(EncryptedSharedPreferences::class.java)

        verify {
            anyConstructed<SafeSharedPreferencesFactory>().create(any(), any()) wasNot Called
            anyConstructed<KeystoreEncryptedSharedPreferencesFactory>().create(any(), any()) wasNot Called
        }
    }

    @Test
    fun test_create_returns_SymmetricKeyEncryptedSharedPreferences_if_type_equals_to_AES() {
        mockkConstructor(SafeSharedPreferencesFactory::class)
        every { anyConstructed<SafeSharedPreferencesFactory>().create(any(), any()) } returns mockk<SafeSharedPreferences>()

        mockkConstructor(KeystoreEncryptedSharedPreferencesFactory::class)
        every { anyConstructed<KeystoreEncryptedSharedPreferencesFactory>().create(any(), any()) } returns mockk<EncryptedSharedPreferences>()

        val config = mockk<EncryptedSharedPreferencesFactory.EncryptedSharedPreferencesConfig>()

        val name = "name"
        val mode = Context.MODE_PRIVATE
        val type =
            EncryptType.SAFE
        val actual = EncryptedSharedPreferencesFactory(
            appContext,
            config
        ).create(name, mode, type)

        assertThat(actual).isInstanceOf(SafeSharedPreferences::class.java)
        assertThat(actual).isNotInstanceOf(EncryptedSharedPreferences::class.java)

        verify {
            anyConstructed<SafeSharedPreferencesFactory>().create(name, mode)
            anyConstructed<KeystoreEncryptedSharedPreferencesFactory>().create(any(), any()) wasNot Called
        }
    }

    @Test
    fun test_create_returns_EncryptedSharedPreferences_if_type_equals_to_KEYSTORE() {
        mockkConstructor(SafeSharedPreferencesFactory::class)
        every { anyConstructed<SafeSharedPreferencesFactory>().create(any(), any()) } returns mockk<SafeSharedPreferences>()

        mockkConstructor(KeystoreEncryptedSharedPreferencesFactory::class)
        every { anyConstructed<KeystoreEncryptedSharedPreferencesFactory>().create(any(), any()) } returns mockk<EncryptedSharedPreferences>()

        val config = mockk<EncryptedSharedPreferencesFactory.EncryptedSharedPreferencesConfig>()

        val name = "name"
        val mode = Context.MODE_PRIVATE
        val type =
            EncryptType.KEYSTORE
        val actual = EncryptedSharedPreferencesFactory(
            appContext,
            config
        ).create(name, mode, type)

        assertThat(actual).isNotInstanceOf(SafeSharedPreferences::class.java)
        assertThat(actual).isInstanceOf(EncryptedSharedPreferences::class.java)

        verify {
            anyConstructed<SafeSharedPreferencesFactory>().create(any(), any()) wasNot Called
            anyConstructed<KeystoreEncryptedSharedPreferencesFactory>().create(name, mode)
        }
    }

    @Test
    fun test_create_transparent_exception_if_exception_thrown_from_KEYSTORE() {
        mockkConstructor(SafeSharedPreferencesFactory::class)
        every { anyConstructed<SafeSharedPreferencesFactory>().create(any(), any()) } returns mockk<SafeSharedPreferences>()

        val exception = IOException()
        mockkConstructor(KeystoreEncryptedSharedPreferencesFactory::class)
        every { anyConstructed<KeystoreEncryptedSharedPreferencesFactory>().create(any(), any()) } throws exception

        val config = mockk<EncryptedSharedPreferencesFactory.EncryptedSharedPreferencesConfig>()

        val name = "name"
        val mode = Context.MODE_PRIVATE
        val type =
            EncryptType.KEYSTORE

        try {
            EncryptedSharedPreferencesFactory(
                appContext,
                config
            ).create(name, mode, type)
            assertThat(true).isFalse() // This line should not be executed
        } catch (e: Exception) {
            assertThat(e).isInstanceOf(IOException::class.java)
        }

        verify {
            anyConstructed<SafeSharedPreferencesFactory>().create(any(), any()) wasNot Called
            anyConstructed<KeystoreEncryptedSharedPreferencesFactory>().create(name, mode)
        }
    }

    @Test
    fun test_tryCreate_returns_target_shared_preferences_if_no_error_thrown() {
        val nonePref = mockk<SharedPreferences>()
        val safePref = mockk<SafeSharedPreferences>()
        val keystorePref = mockk<EncryptedSharedPreferences>()

        val config = mockk<EncryptedSharedPreferencesFactory.EncryptedSharedPreferencesConfig>()

        val factory = spyk(
            EncryptedSharedPreferencesFactory(
                appContext,
                config
            )
        )
        every { factory.create(any(), any(),
            EncryptType.NONE
        ) } returns nonePref
        every { factory.create(any(), any(),
            EncryptType.SAFE
        ) } returns safePref
        every { factory.create(any(), any(),
            EncryptType.KEYSTORE
        ) } returns keystorePref

        val name = "name"
        val mode = Context.MODE_PRIVATE
        val type =
            EncryptType.KEYSTORE // you can choose either NORMAL, AES, or KEYSTORE
        val (actualPref, actualType) = factory.tryCreate(name, mode, type)

        verify(exactly = 1) {
            factory.create(name, mode, any())
        }

        assertThat(actualPref).isEqualTo(keystorePref)
        assertThat(actualType).isEqualTo(type)
    }

    @Test
    fun test_tryCreate_returns_downgraded1_shared_preferences_if_error_thrown1() {
        val nonePref = mockk<SharedPreferences>()
        val safePref = mockk<SafeSharedPreferences>()

        val config = mockk<EncryptedSharedPreferencesFactory.EncryptedSharedPreferencesConfig>()

        val factory = spyk(
            EncryptedSharedPreferencesFactory(
                appContext,
                config
            )
        )
        every { factory.create(any(), any(),
            EncryptType.NONE
        ) } returns nonePref
        every { factory.create(any(), any(),
            EncryptType.SAFE
        ) } returns safePref
        every { factory.create(any(), any(),
            EncryptType.KEYSTORE
        ) } throws IOException()

        val name = "name"
        val mode = Context.MODE_PRIVATE
        val type =
            EncryptType.KEYSTORE // you can choose either NORMAL, AES, or KEYSTORE
        val (actualPref, actualType) = factory.tryCreate(name, mode, type)

        verify(exactly = 1) {
            factory.create(name, mode,
                EncryptType.KEYSTORE
            )
            factory.create(name, mode,
                EncryptType.SAFE
            )
        }

        assertThat(actualPref).isEqualTo(safePref)
        assertThat(actualType).isEqualTo(EncryptType.SAFE)
    }

    @Test
    fun test_tryCreate_returns_downgraded2_shared_preferences_if_error_thrown2() {
        val nonePref = mockk<SharedPreferences>()

        val config = mockk<EncryptedSharedPreferencesFactory.EncryptedSharedPreferencesConfig>()

        val factory = spyk(
            EncryptedSharedPreferencesFactory(
                appContext,
                config
            )
        )
        every { factory.create(any(), any(),
            EncryptType.NONE
        ) } returns nonePref
        every { factory.create(any(), any(),
            EncryptType.SAFE
        ) } throws IOException()
        every { factory.create(any(), any(),
            EncryptType.KEYSTORE
        ) } throws IOException()

        val name = "name"
        val mode = Context.MODE_PRIVATE
        val type =
            EncryptType.KEYSTORE // you can choose either NORMAL, AES, or KEYSTORE
        val (actualPref, actualType) = factory.tryCreate(name, mode, type)

        verify(exactly = 1) {
            factory.create(name, mode,
                EncryptType.KEYSTORE
            )
            factory.create(name, mode,
                EncryptType.SAFE
            )
            factory.create(name, mode,
                EncryptType.NONE
            )
        }

        assertThat(actualPref).isEqualTo(nonePref)
        assertThat(actualType).isEqualTo(EncryptType.NONE)
    }

    @Test
    fun test_tryCreate_throws_exception_if_all_creation_failed() {
        val config = mockk<EncryptedSharedPreferencesFactory.EncryptedSharedPreferencesConfig>()

        val exception = IOException()
        val factory = spyk(
            EncryptedSharedPreferencesFactory(
                appContext,
                config
            )
        )

        every { factory.create(any(), any(),
            EncryptType.NONE
        ) } throws exception
        every { factory.create(any(), any(),
            EncryptType.SAFE
        ) } throws exception
        every { factory.create(any(), any(),
            EncryptType.KEYSTORE
        ) } throws exception

        val name = "name"
        val mode = Context.MODE_PRIVATE
        val type =
            EncryptType.KEYSTORE // you can choose either NORMAL, AES, or KEYSTORE

        try {
            factory.tryCreate(name, mode, type)
            assertThat(true).isFalse() // This line should not be executed
        } catch (e: Throwable) {
            assertThat(e).isInstanceOf(IOException::class.java)
        }
    }

    @Test
    fun test_create_returns_target_shared_preferences_if_currentEncryptType_exists_and_creation_succeeds() {
        val name = "name"
        val mode = Context.MODE_PRIVATE
        val type =
            EncryptType.KEYSTORE
        val config = mockk<EncryptedSharedPreferencesFactory.EncryptedSharedPreferencesConfig>()
        every { config getProperty "currentEncryptType" } returns type

        val factory = spyk(
            EncryptedSharedPreferencesFactory(
                appContext,
                config
            )
        )
        val preferences = mockk<EncryptedSharedPreferences>()
        every { factory.create(name, mode, type) } returns preferences

        val actual = factory.create(name, mode)

        assertThat(actual).isEqualTo(preferences)

        verify(exactly = 1) {
            factory.create(name, mode, type)
        }
    }

    @Test
    fun test_create_throws_exception_if_currentEncryptType_exists_and_creation_fails() {
        val name = "name"
        val mode = Context.MODE_PRIVATE
        val type =
            EncryptType.KEYSTORE
        val config = mockk<EncryptedSharedPreferencesFactory.EncryptedSharedPreferencesConfig>()
        every { config getProperty "currentEncryptType" } returns type

        val factory = spyk(
            EncryptedSharedPreferencesFactory(
                appContext,
                config
            )
        )
        val exception = IOException()
        every { factory.create(name, mode, type) } throws exception

        try {
            factory.create(name, mode)
            assertThat(true).isFalse() // This line should not be executed
        } catch (e: Exception) {
            assertThat(e).isEqualTo(exception)
        }

        verify(exactly = 1) {
            factory.create(name, mode, type)
        }
    }

    @Test
    fun test_create_returns_proper_shared_preferences_if_currentEncryptType_not_exists_and_creation_succeeds() {
        val name = "name"
        val mode = Context.MODE_PRIVATE
        val targetType =
            EncryptType.KEYSTORE
        val config = mockk<EncryptedSharedPreferencesFactory.EncryptedSharedPreferencesConfig>()
        every { config getProperty "currentEncryptType" } returns null
        every { config setProperty "currentEncryptType" value any<EncryptType>() } just Runs

        val factory = spyk(
            EncryptedSharedPreferencesFactory(
                appContext,
                config
            )
        )
        every { factory getProperty "topEncryptType" } returns targetType
        val preferences = mockk<SafeSharedPreferences>()
        val properType =
            EncryptType.NONE
        every { factory.tryCreate(name, mode, targetType) } returns (preferences to properType)

        val actual = factory.create(name, mode)

        assertThat(actual).isEqualTo(preferences)

        verify(exactly = 1) {
            factory.tryCreate(name, mode, targetType)
            config setProperty "currentEncryptType" value properType
        }
    }

    @Test
    fun test_create_throws_exception_if_currentEncryptType_not_exists_and_creation_failed() {
        val name = "name"
        val mode = Context.MODE_PRIVATE
        val targetType =
            EncryptType.KEYSTORE
        val config = mockk<EncryptedSharedPreferencesFactory.EncryptedSharedPreferencesConfig>()
        every { config getProperty "currentEncryptType" } returns null
        every { config setProperty "currentEncryptType" value any<EncryptType>() } just Runs

        val factory = spyk(
            EncryptedSharedPreferencesFactory(
                appContext,
                config
            )
        )
        every { factory getProperty "topEncryptType" } returns targetType
        val exception = IOException()
        every { factory.tryCreate(name, mode, targetType) } throws exception

        try {
            factory.create(name, mode)
            assertThat(true).isFalse() // This line should not be executed
        } catch (e: Exception) {
            assertThat(e).isEqualTo(exception)
        }

        verify {
            factory.tryCreate(name, mode, targetType)
        }
        verify(exactly = 0) {
            // was not called
            // https://github.com/mockk/mockk/issues/349
            config setProperty "currentEncryptType" value any<EncryptType>()
        }
    }

    @Test
    fun test_create_returns_specific_type_SharedPreferences() {
        val name = "name"
        val mode = Context.MODE_PRIVATE
        val targetType =
            EncryptType.KEYSTORE
        val config = mockk<EncryptedSharedPreferencesFactory.EncryptedSharedPreferencesConfig>()

        mockkConstructor(SafeSharedPreferencesFactory::class)
        every { anyConstructed<SafeSharedPreferencesFactory>().create(any(), any()) } returns mockk<SafeSharedPreferences>()

        mockkConstructor(KeystoreEncryptedSharedPreferencesFactory::class)
        every { anyConstructed<KeystoreEncryptedSharedPreferencesFactory>().create(any(), any()) } returns mockk<EncryptedSharedPreferences>()

        val factory = spyk(
            EncryptedSharedPreferencesFactory(
                appContext,
                config
            )
        )
        val actual = factory.create(name, mode, targetType)

        assertThat(actual).isInstanceOf(EncryptedSharedPreferences::class.java)

        verify(exactly = 1) {
            anyConstructed<KeystoreEncryptedSharedPreferencesFactory>().create(name, mode)
        }
        verify(exactly = 0) {
            anyConstructed<SafeSharedPreferencesFactory>().create(any(), any())
        }
    }

    @Test
    fun test_create_throws_exception_if_creation_failed() {
        val name = "name"
        val mode = Context.MODE_PRIVATE
        val targetType =
            EncryptType.KEYSTORE
        val config = mockk<EncryptedSharedPreferencesFactory.EncryptedSharedPreferencesConfig>()

        mockkConstructor(SafeSharedPreferencesFactory::class)
        every { anyConstructed<SafeSharedPreferencesFactory>().create(any(), any()) } returns mockk<SafeSharedPreferences>()

        val exception = IOException()
        mockkConstructor(KeystoreEncryptedSharedPreferencesFactory::class)
        every { anyConstructed<KeystoreEncryptedSharedPreferencesFactory>().create(any(), any()) } throws exception

        val factory = spyk(
            EncryptedSharedPreferencesFactory(
                appContext,
                config
            )
        )

        try {
            factory.create(name, mode, targetType)
            assertThat(true).isFalse() // This line should not be executed
        } catch (e: Exception) {
            assertThat(e).isEqualTo(exception)
        }
    }
}

@RunWith(AndroidJUnit4::class)
class EncryptedSharedPreferencesConfigTest {

    @Test
    fun test_currentEncryptType_get_returns_null_if_type_is_null() {
        val preferences = mockk<SharedPreferences>()
        every { preferences.getString(any(), any()) } returns null
        val context = spyk(InstrumentationRegistry.getInstrumentation().targetContext)
        every { context.getSharedPreferences(any(), any()) } returns preferences

        val config = EncryptedSharedPreferencesFactory.EncryptedSharedPreferencesConfig(context)
        val actual = config.currentEncryptType

        assertThat(actual).isNull()
    }

    @Test
    fun test_currentEncryptType_get_returns_null_if_type_is_empty() {
        val preferences = mockk<SharedPreferences>()
        every { preferences.getString(any(), any()) } returns ""
        val context = spyk(InstrumentationRegistry.getInstrumentation().targetContext)
        every { context.getSharedPreferences(any(), any()) } returns preferences

        val config = EncryptedSharedPreferencesFactory.EncryptedSharedPreferencesConfig(context)
        val actual = config.currentEncryptType

        assertThat(actual).isNull()
    }

    @Test
    fun test_currentEncryptType_get_returns_NORMAL_if_type_is_NONE() {
        val preferences = mockk<SharedPreferences>()
        every { preferences.getString(any(), any()) } returns "NONE"
        val context = spyk(InstrumentationRegistry.getInstrumentation().targetContext)
        every { context.getSharedPreferences(any(), any()) } returns preferences

        val config = EncryptedSharedPreferencesFactory.EncryptedSharedPreferencesConfig(context)
        val actual = config.currentEncryptType

        assertThat(actual).isEqualTo(EncryptType.NONE)
    }

    @Test
    fun test_currentEncryptType_get_returns_AES_if_type_is_SAFE() {
        val preferences = mockk<SharedPreferences>()
        every { preferences.getString(any(), any()) } returns "SAFE"
        val context = spyk(InstrumentationRegistry.getInstrumentation().targetContext)
        every { context.getSharedPreferences(any(), any()) } returns preferences

        val config = EncryptedSharedPreferencesFactory.EncryptedSharedPreferencesConfig(context)
        val actual = config.currentEncryptType

        assertThat(actual).isEqualTo(EncryptType.SAFE)
    }

    @Test
    fun test_currentEncryptType_get_returns_KEYSTORE_if_type_is_KEYSTORE() {
        val preferences = mockk<SharedPreferences>()
        every { preferences.getString(any(), any()) } returns "KEYSTORE"
        val context = spyk(InstrumentationRegistry.getInstrumentation().targetContext)
        every { context.getSharedPreferences(any(), any()) } returns preferences

        val config = EncryptedSharedPreferencesFactory.EncryptedSharedPreferencesConfig(context)
        val actual = config.currentEncryptType

        assertThat(actual).isEqualTo(EncryptType.KEYSTORE)
    }

    @Test
    fun test_currentEncryptType_get_returns_null_if_type_is_UNKNOWN_VALUE() {
        val preferences = mockk<SharedPreferences>()
        every { preferences.getString(any(), any()) } returns "some unknown value"
        val context = spyk(InstrumentationRegistry.getInstrumentation().targetContext)
        every { context.getSharedPreferences(any(), any()) } returns preferences

        val config = EncryptedSharedPreferencesFactory.EncryptedSharedPreferencesConfig(context)
        val actual = config.currentEncryptType

        assertThat(actual).isNull()
    }

    @Test
    fun test_currentEncryptType_set_just_run_if_commit_succeeded() {
        val preferences = mockk<SharedPreferences>()
        val editor = mockk<SharedPreferences.Editor>()
        every { preferences.edit() } returns editor
        every { editor.putString(any(), any()) } returns editor
        every { editor.commit() } returns true
        val context = spyk(InstrumentationRegistry.getInstrumentation().targetContext)
        every { context.getSharedPreferences(any(), any()) } returns preferences

        val config = EncryptedSharedPreferencesFactory.EncryptedSharedPreferencesConfig(context)
        config.currentEncryptType =
            EncryptType.KEYSTORE

        verify(exactly = 1) {
            editor.putString("encrypt_type", "KEYSTORE")
            editor.commit()
        }
    }

    @Test
    fun test_currentEncryptType_set_throws_exception_if_commit_failed() {
        val preferences = mockk<SharedPreferences>()
        val editor = mockk<SharedPreferences.Editor>()
        every { preferences.edit() } returns editor
        every { editor.putString(any(), any()) } returns editor
        every { editor.commit() } returns false
        val context = spyk(InstrumentationRegistry.getInstrumentation().targetContext)
        every { context.getSharedPreferences(any(), any()) } returns preferences

        val config = EncryptedSharedPreferencesFactory.EncryptedSharedPreferencesConfig(context)

        try {
            config.currentEncryptType =
                EncryptType.KEYSTORE
            assertThat(false).isTrue() // This line should not be executed
        } catch (e: Exception) {
            assertThat(e).isInstanceOf(IOException::class.java)
        }

        verify(exactly = 1) {
            editor.putString("encrypt_type", "KEYSTORE")
            editor.commit()
        }
    }
}
