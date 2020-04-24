package com.github.ibara1454.secure_shared_preferences

import android.content.SharedPreferences
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.ibara1454.secure_shared_preferences.cipher.Decrypter
import com.github.ibara1454.secure_shared_preferences.cipher.Encrypter

import org.junit.Test
import org.junit.runner.RunWith

import com.google.common.truth.Truth.assertThat
import io.mockk.*

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class SymmetricKeyEncryptedSharedPreferencesTest {
    // Context of the app under test.
    private val appContext = InstrumentationRegistry.getInstrumentation().targetContext

    @Test
    fun test_contains_returns_true_when_name_exist() {
        val storage = mockk<SharedPreferences>()
        val names = listOf("name_encrypted")
        every { storage.contains(any()) } answers { names.contains(firstArg()) }

        val encrypter = mockk<Encrypter<String>>()
        every { encrypter.encrypt(any()) } answers { "${firstArg<String>()}_encrypted" }

        val decrypter = mockk<Decrypter<String>>()

        val sharedPrefs = SymmetricKeyEncryptedSharedPreferences(storage, encrypter, decrypter)
        val actual = sharedPrefs.contains("name")

        assertThat(actual).isTrue()
    }

    @Test
    fun test_edit_returns_nonnull_editor() {
        val storage = mockk<SharedPreferences>()
        val dummyEditor = mockk<SharedPreferences.Editor>()
        every { storage.edit() } returns dummyEditor

        val encrypter = mockk<Encrypter<String>>()

        val decrypter = mockk<Decrypter<String>>()

        val sharedPrefs = SymmetricKeyEncryptedSharedPreferences(storage, encrypter, decrypter)
        val actual = sharedPrefs.edit()

        assertThat(actual).isInstanceOf(SymmetricKeyEncryptedSharedPreferences.EditorImpl::class.java)
    }

    @Test
    fun test_getAll_returns_all_stored_value_with_correct_type() {
        val storage = mockk<SharedPreferences>()
        every { storage.all } returns mutableMapOf(
            "boolean_name1" to "true",
            "float_name2" to "2.0",
            "int_name3" to "3",
            "long_name4" to "4",
            "string_name5" to "5",
            "stringset_name6" to "6;7;8"
        )

        val encrypter = mockk<Encrypter<String>>()

        val decrypter = mockk<Decrypter<String>>()
        every { decrypter.decrypt(any()) } answers { firstArg() }

        val sharedPrefs = SymmetricKeyEncryptedSharedPreferences(storage, encrypter, decrypter)
        val actual = sharedPrefs.all

        assertThat(actual).isEqualTo(mutableMapOf(
            "name1" to true,
            "name2" to 2.0f,
            "name3" to 3,
            "name4" to 4L,
            "name5" to "5",
            "name6" to mutableSetOf("6", "7", "8")
        ))
    }

    @Test
    fun test_getBoolean_returns_stored_value_when_name_exist() {
        val storage = mockk<SharedPreferences>()
        every { storage.getString(any(), any()) } answers { secondArg() }
        every { storage.getString("encrypted_boolean_name", any()) } answers { "encrypted_boolean_value" }

        val encrypter = mockk<Encrypter<String>>()
        every { encrypter.encrypt(any()) } answers { "encrypted_" + firstArg() }

        val decrypter = mockk<Decrypter<String>>()
        every { decrypter.decrypt("encrypted_boolean_value") } answers { "true" }

        val sharedPrefs = SymmetricKeyEncryptedSharedPreferences(storage, encrypter, decrypter)
        val actual = sharedPrefs.getBoolean("name", false)

        assertThat(actual).isTrue()
    }

    @Test
    fun test_getBoolean_returns_defValue_when_name_not_exist() {
        val storage = mockk<SharedPreferences>()
        every { storage.getString(any(), any()) } answers { secondArg() }
        every { storage.getString("encrypted_boolean_name", any()) } answers { "encrypted_boolean_value" }

        val encrypter = mockk<Encrypter<String>>()
        every { encrypter.encrypt(any()) } answers { "encrypted_" + firstArg() }

        val decrypter = mockk<Decrypter<String>>()
        every { decrypter.decrypt("encrypted_boolean_value") } answers { "true" }

        val sharedPrefs = SymmetricKeyEncryptedSharedPreferences(storage, encrypter, decrypter)
        val actual = sharedPrefs.getBoolean("name_not_exist", false)

        assertThat(actual).isFalse()
    }

    @Test
    fun test_getFloat_returns_stored_value_when_name_exist() {
        val storage = mockk<SharedPreferences>()
        every { storage.getString(any(), any()) } answers { secondArg() }
        every { storage.getString("encrypted_float_name", any()) } answers { "encrypted_float_value" }

        val encrypter = mockk<Encrypter<String>>()
        every { encrypter.encrypt(any()) } answers { "encrypted_" + firstArg() }

        val decrypter = mockk<Decrypter<String>>()
        every { decrypter.decrypt("encrypted_float_value") } answers { "1.0" }

        val sharedPrefs = SymmetricKeyEncryptedSharedPreferences(storage, encrypter, decrypter)
        val actual = sharedPrefs.getFloat("name", 0f)

        assertThat(actual).isEqualTo(1.0f)
    }

    @Test
    fun test_getFloat_returns_defValue_when_name_not_exist() {
        val storage = mockk<SharedPreferences>()
        every { storage.getString(any(), any()) } answers { secondArg() }
        every { storage.getString("encrypted_float_name", any()) } answers { "encrypted_float_value" }

        val encrypter = mockk<Encrypter<String>>()
        every { encrypter.encrypt(any()) } answers { "encrypted_" + firstArg() }

        val decrypter = mockk<Decrypter<String>>()
        every { decrypter.decrypt("encrypted_float_value") } answers { "1.0" }

        val sharedPrefs = SymmetricKeyEncryptedSharedPreferences(storage, encrypter, decrypter)
        val actual = sharedPrefs.getFloat("name_not_exist", 0f)

        assertThat(actual).isEqualTo(0.0f)
    }

    @Test
    fun test_getInt_returns_stored_value_when_name_exist() {
        val storage = mockk<SharedPreferences>()
        every { storage.getString(any(), any()) } answers { secondArg() }
        every { storage.getString("encrypted_int_name", any()) } answers { "encrypted_int_value" }

        val encrypter = mockk<Encrypter<String>>()
        every { encrypter.encrypt(any()) } answers { "encrypted_" + firstArg() }

        val decrypter = mockk<Decrypter<String>>()
        every { decrypter.decrypt("encrypted_int_value") } answers { "1" }

        val sharedPrefs = SymmetricKeyEncryptedSharedPreferences(storage, encrypter, decrypter)
        val actual = sharedPrefs.getInt("name", 0)

        assertThat(actual).isEqualTo(1)
    }

    @Test
    fun test_getInt_returns_defValue_when_name_not_exist() {
        val storage = mockk<SharedPreferences>()
        every { storage.getString(any(), any()) } answers { secondArg() }
        every { storage.getString("encrypted_int_name", any()) } answers { "encrypted_int_value" }

        val encrypter = mockk<Encrypter<String>>()
        every { encrypter.encrypt(any()) } answers { "encrypted_" + firstArg() }

        val decrypter = mockk<Decrypter<String>>()
        every { decrypter.decrypt("encrypted_int_value") } answers { "1" }

        val sharedPrefs = SymmetricKeyEncryptedSharedPreferences(storage, encrypter, decrypter)
        val actual = sharedPrefs.getInt("name_not_exist", 0)

        assertThat(actual).isEqualTo(0)
    }

    @Test
    fun test_getLong_returns_stored_value_when_name_exist() {
        val storage = mockk<SharedPreferences>()
        every { storage.getString(any(), any()) } answers { secondArg() }
        every { storage.getString("encrypted_long_name", any()) } answers { "encrypted_long_value" }

        val encrypter = mockk<Encrypter<String>>()
        every { encrypter.encrypt(any()) } answers { "encrypted_" + firstArg() }

        val decrypter = mockk<Decrypter<String>>()
        every { decrypter.decrypt("encrypted_long_value") } answers { "1" }

        val sharedPrefs = SymmetricKeyEncryptedSharedPreferences(storage, encrypter, decrypter)
        val actual = sharedPrefs.getLong("name", 0L)

        assertThat(actual).isEqualTo(1)
    }

    @Test
    fun test_getLong_returns_defValue_when_name_not_exist() {
        val storage = mockk<SharedPreferences>()
        every { storage.getString(any(), any()) } answers { secondArg() }
        every { storage.getString("encrypted_long_name", any()) } answers { "encrypted_long_value" }

        val encrypter = mockk<Encrypter<String>>()
        every { encrypter.encrypt(any()) } answers { "encrypted_" + firstArg() }

        val decrypter = mockk<Decrypter<String>>()
        every { decrypter.decrypt("encrypted_long_value") } answers { "1" }

        val sharedPrefs = SymmetricKeyEncryptedSharedPreferences(storage, encrypter, decrypter)
        val actual = sharedPrefs.getLong("name_not_exist", 0L)

        assertThat(actual).isEqualTo(0L)
    }

    @Test
    fun test_getString_stored_value_when_name_exist() {
        val storage = mockk<SharedPreferences>()
        every { storage.getString(any(), any()) } answers { secondArg() }
        every { storage.getString("encrypted_string_name", any()) } answers { "encrypted_string_value" }

        val encrypter = mockk<Encrypter<String>>()
        every { encrypter.encrypt(any()) } answers { "encrypted_" + firstArg() }

        val decrypter = mockk<Decrypter<String>>()
        every { decrypter.decrypt("encrypted_string_value") } answers { "string_value" }

        val sharedPrefs = SymmetricKeyEncryptedSharedPreferences(storage, encrypter, decrypter)
        val actual = sharedPrefs.getString("name", "defValue")

        assertThat(actual).isEqualTo("string_value")
    }

    @Test
    fun test_getString_defValue_when_name_not_exist() {
        val storage = mockk<SharedPreferences>()
        every { storage.getString(any(), any()) } answers { secondArg() }
        every { storage.getString("encrypted_string_name", any()) } answers { "encrypted_string_value" }

        val encrypter = mockk<Encrypter<String>>()
        every { encrypter.encrypt(any()) } answers { "encrypted_" + firstArg() }

        val decrypter = mockk<Decrypter<String>>()
        every { decrypter.decrypt("encrypted_string_value") } answers { "string_value" }

        val sharedPrefs = SymmetricKeyEncryptedSharedPreferences(storage, encrypter, decrypter)
        val actual = sharedPrefs.getString("name_not_exist", null)

        assertThat(actual).isEqualTo(null)
    }

    @Test
    fun test_getStringSet_stored_value_when_name_exist() {
        val storage = mockk<SharedPreferences>()
        every { storage.getString(any(), any()) } answers { secondArg() }
        every { storage.getString("encrypted_stringset_name", any()) } answers { "encrypted_stringset_value" }

        val encrypter = mockk<Encrypter<String>>()
        every { encrypter.encrypt(any()) } answers { "encrypted_" + firstArg() }

        val decrypter = mockk<Decrypter<String>>()
        every { decrypter.decrypt("encrypted_stringset_value") } answers { "1;2;3" }

        val sharedPrefs = SymmetricKeyEncryptedSharedPreferences(storage, encrypter, decrypter)
        val actual = sharedPrefs.getStringSet("name", null)

        assertThat(actual).isEqualTo(mutableSetOf("1", "2", "3"))
    }

    @Test
    fun test_getStringSet_defValue_when_name_not_exist() {
        val storage = mockk<SharedPreferences>()
        every { storage.getString(any(), any()) } answers { secondArg() }
        every { storage.getString("encrypted_stringset_name", any()) } answers { "encrypted_stringset_value" }

        val encrypter = mockk<Encrypter<String>>()
        every { encrypter.encrypt(any()) } answers { "encrypted_" + firstArg() }

        val decrypter = mockk<Decrypter<String>>()
        every { decrypter.decrypt("encrypted_stringset_value") } answers { "1;2;3" }

        val sharedPrefs = SymmetricKeyEncryptedSharedPreferences(storage, encrypter, decrypter)
        val actual = sharedPrefs.getStringSet("name_not_exist", null)

        assertThat(actual).isEqualTo(null)
    }

    @Test
    fun test_registerOnSharedPreferenceChangeListener() {
        // Problem(2020.04.27): Mockk doesn't support private property mocks
        // See https://github.com/mockk/mockk/issues/104
        // val encrypter = mockk<Encrypter>()
        //
        // val storage = mockk<SharedPreferences>()
        // every { storage.registerOnSharedPreferenceChangeListener(any()) } just Runs
        //
        // val listenerMaps = mutableMapOf<SharedPreferences.OnSharedPreferenceChangeListener, SharedPreferences.OnSharedPreferenceChangeListener>()
        //
        // val comp = spyk(SymmetricKeyEncryptedSharedPreferences.Companion, recordPrivateCalls = true)
        // every { comp getProperty "listenerMap" } propertyType listenerMaps::class returns listenerMaps
        //
        // val sharedPrefs = SymmetricKeyEncryptedSharedPreferences(encrypter, storage)
        // sharedPrefs.registerOnSharedPreferenceChangeListener { _, _ -> }
        //
        // assertThat(listenerMaps).isNotEmpty()
    }

    @Test
    fun test_unregisterOnSharedPreferenceChangeListener() {
        // Problem(2020.04.27): Mockk doesn't support private property mocks
        // See https://github.com/mockk/mockk/issues/104
    }

    @RunWith(AndroidJUnit4::class)
    class EditorImplTest {
        @Test
        fun test_apply_is_transparently() {
            val encrypter = mockk<Encrypter<String>>()

            val nativeEditor = mockk<SharedPreferences.Editor>()
            every { nativeEditor.apply() } just Runs

            val editor = SymmetricKeyEncryptedSharedPreferences.EditorImpl(nativeEditor, encrypter)
            editor.apply()

            verify(exactly = 1) { nativeEditor.apply() }
        }

        @Test
        fun test_clear_is_transparently() {
            val encrypter = mockk<Encrypter<String>>()

            val nativeEditor = mockk<SharedPreferences.Editor>()
            every { nativeEditor.clear() } returns nativeEditor

            val editor = SymmetricKeyEncryptedSharedPreferences.EditorImpl(nativeEditor, encrypter)
            editor.clear()

            verify(exactly = 1) { nativeEditor.clear() }
        }

        @Test
        fun test_commit_is_transparently() {
            val encrypter = mockk<Encrypter<String>>()

            val nativeEditor = mockk<SharedPreferences.Editor>()
            every { nativeEditor.commit() } returns true

            val editor = SymmetricKeyEncryptedSharedPreferences.EditorImpl(nativeEditor, encrypter)
            val actual = editor.commit()

            verify { nativeEditor.commit() }
            assertThat(actual).isTrue()
        }

        @Test
        fun test_putBoolean_put_encrypted_value_into_storage_if_input_is_nonnull() {
            val encrypter = mockk<Encrypter<String>>()
            every { encrypter.encrypt(any()) } answers { "encrypted_" + firstArg() }

            val nativeEditor = mockk<SharedPreferences.Editor>()
            every { nativeEditor.putString(any(), any()) } returns nativeEditor

            val editor = SymmetricKeyEncryptedSharedPreferences.EditorImpl(nativeEditor, encrypter)
            editor.putBoolean("name", true)

            verifyAll {
                encrypter.encrypt("boolean_name")
                encrypter.encrypt("true")
                nativeEditor.putString("encrypted_boolean_name", "encrypted_true")
            }
        }

        @Test
        fun test_putFloat_put_encrypted_value_into_storage_if_input_is_nonnull() {
            val encrypter = mockk<Encrypter<String>>()
            every { encrypter.encrypt(any()) } answers { "encrypted_" + firstArg() }

            val nativeEditor = mockk<SharedPreferences.Editor>()
            every { nativeEditor.putString(any(), any()) } returns nativeEditor

            val editor = SymmetricKeyEncryptedSharedPreferences.EditorImpl(nativeEditor, encrypter)
            editor.putFloat("name", 1.0f)

            verifyAll {
                encrypter.encrypt("float_name")
                encrypter.encrypt("1.0")
                nativeEditor.putString("encrypted_float_name", "encrypted_1.0")
            }
        }

        @Test
        fun test_putInt_put_encrypted_value_into_storage_if_input_is_nonnull() {
            val encrypter = mockk<Encrypter<String>>()
            every { encrypter.encrypt(any()) } answers { "encrypted_" + firstArg() }

            val nativeEditor = mockk<SharedPreferences.Editor>()
            every { nativeEditor.putString(any(), any()) } returns nativeEditor

            val editor = SymmetricKeyEncryptedSharedPreferences.EditorImpl(nativeEditor, encrypter)
            editor.putInt("name", 1)

            verifyAll {
                encrypter.encrypt("int_name")
                encrypter.encrypt("1")
                nativeEditor.putString("encrypted_int_name", "encrypted_1")
            }
        }

        @Test
        fun test_putLong_put_encrypted_value_into_storage_if_input_is_nonnull() {
            val encrypter = mockk<Encrypter<String>>()
            every { encrypter.encrypt(any()) } answers { "encrypted_" + firstArg() }

            val nativeEditor = mockk<SharedPreferences.Editor>()
            every { nativeEditor.putString(any(), any()) } returns nativeEditor

            val editor = SymmetricKeyEncryptedSharedPreferences.EditorImpl(nativeEditor, encrypter)
            editor.putLong("name", 1L)

            verifyAll {
                encrypter.encrypt("long_name")
                encrypter.encrypt("1")
                nativeEditor.putString("encrypted_long_name", "encrypted_1")
            }
        }

        @Test
        fun test_putString_put_encrypted_value_into_storage_if_input_is_nonnull() {
            val encrypter = mockk<Encrypter<String>>()
            every { encrypter.encrypt(any()) } answers { "encrypted_" + firstArg() }

            val nativeEditor = mockk<SharedPreferences.Editor>()
            every { nativeEditor.putString(any(), any()) } returns nativeEditor

            val editor = SymmetricKeyEncryptedSharedPreferences.EditorImpl(nativeEditor, encrypter)
            editor.putString("name", "value")

            verifyAll {
                encrypter.encrypt("string_name")
                encrypter.encrypt("value")
                nativeEditor.putString("encrypted_string_name", "encrypted_value")
            }
        }

        @Test
        fun test_putString_put_null_into_storage_if_input_is_null() {
            val encrypter = mockk<Encrypter<String>>()
            every { encrypter.encrypt(any()) } answers { "encrypted_" + firstArg() }

            val nativeEditor = mockk<SharedPreferences.Editor>()
            every { nativeEditor.putString(any(), any()) } returns nativeEditor

            val editor = SymmetricKeyEncryptedSharedPreferences.EditorImpl(nativeEditor, encrypter)
            editor.putString("name", null)

            verifyAll {
                encrypter.encrypt("string_name")
                nativeEditor.putString("encrypted_string_name", null)
            }
        }

        @Test
        fun test_putStringSet_put_encrypted_value_into_storage_if_input_is_not_empty() {
            val encrypter = mockk<Encrypter<String>>()
            every { encrypter.encrypt(any()) } answers { "encrypted_" + firstArg() }

            val nativeEditor = mockk<SharedPreferences.Editor>()
            every { nativeEditor.putString(any(), any()) } returns nativeEditor

            val editor = SymmetricKeyEncryptedSharedPreferences.EditorImpl(nativeEditor, encrypter)
            editor.putStringSet("name", mutableSetOf("1", "2", "3"))

            verifyAll {
                encrypter.encrypt("stringset_name")
                encrypter.encrypt("1;2;3")
                nativeEditor.putString("encrypted_stringset_name", "encrypted_1;2;3")
            }
        }

        @Test
        fun test_putStringSet_put_encrypted_value_into_storage_if_input_is_empty() {
            val encrypter = mockk<Encrypter<String>>()
            every { encrypter.encrypt(any()) } answers { "encrypted_" + firstArg() }

            val nativeEditor = mockk<SharedPreferences.Editor>()
            every { nativeEditor.putString(any(), any()) } returns nativeEditor

            val editor = SymmetricKeyEncryptedSharedPreferences.EditorImpl(nativeEditor, encrypter)
            editor.putStringSet("name", mutableSetOf())

            verifyAll {
                encrypter.encrypt("stringset_name")
                encrypter.encrypt("")
                nativeEditor.putString("encrypted_stringset_name", "encrypted_")
            }
        }

        @Test
        fun test_putStringSet_put_null_into_storage_if_input_is_null() {
            val encrypter = mockk<Encrypter<String>>()
            every { encrypter.encrypt(any()) } answers { "encrypted_" + firstArg() }

            val nativeEditor = mockk<SharedPreferences.Editor>()
            every { nativeEditor.putString(any(), any()) } returns nativeEditor

            val editor = SymmetricKeyEncryptedSharedPreferences.EditorImpl(nativeEditor, encrypter)
            editor.putStringSet("name", null)

            verifyAll {
                encrypter.encrypt("stringset_name")
                nativeEditor.putString("encrypted_stringset_name", null)
            }
        }

        @Test
        fun test_remove_is_transparently() {
            val encrypter = mockk<Encrypter<String>>()

            val nativeEditor = mockk<SharedPreferences.Editor>()
            every { nativeEditor.clear() } returns nativeEditor

            val editor = SymmetricKeyEncryptedSharedPreferences.EditorImpl(nativeEditor, encrypter)
            editor.clear()

            verify(exactly = 1) { nativeEditor.clear() }
        }
    }
}
