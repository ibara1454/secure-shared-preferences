package com.github.ibara1454.secure_shared_preferences.shared_preferences

import android.content.SharedPreferences
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
class EncryptableSharedPreferencesTest {
    @Test
    fun test_contains_returns_true_when_name_exist() {
        val preferences = mockk<SharedPreferences>()
        val names = listOf("name_encrypted")
        every { preferences.contains(any()) } answers { names.contains(firstArg()) }

        val prefNameEncrypter = mockk<Encrypter<String>>()
        every { prefNameEncrypter.encrypt(any()) } answers { "${firstArg<String>()}_encrypted" }

        val prefNameDecrypter = mockk<Decrypter<String>>()

        val prefValueEncrypter = mockk<Encrypter<String>>()

        val prefValueDecrypter = mockk<Decrypter<String>>()

        val sharedPrefs = EncryptableSharedPreferences(preferences, prefNameEncrypter, prefNameDecrypter, prefValueEncrypter, prefValueDecrypter)
        val actual = sharedPrefs.contains("name")

        assertThat(actual).isTrue()
    }

    @Test
    fun test_edit_returns_nonnull_editor() {
        val preferences = mockk<SharedPreferences>()
        val dummyEditor = mockk<SharedPreferences.Editor>()
        every { preferences.edit() } returns dummyEditor

        val prefNameEncrypter = mockk<Encrypter<String>>()

        val prefNameDecrypter = mockk<Decrypter<String>>()

        val prefValueEncrypter = mockk<Encrypter<String>>()

        val prefValueDecrypter = mockk<Decrypter<String>>()

        val sharedPrefs = EncryptableSharedPreferences(preferences, prefNameEncrypter, prefNameDecrypter, prefValueEncrypter, prefValueDecrypter)
        val actual = sharedPrefs.edit()

        assertThat(actual).isInstanceOf(EncryptableSharedPreferences.EditorImpl::class.java)
    }

    @Test
    fun test_getAll_returns_all_stored_value_with_correct_type() {
        val preferences = mockk<SharedPreferences>()
        every { preferences.all } returns mutableMapOf(
            "boolean_name1" to "true",
            "float_name2" to "2.0",
            "int_name3" to "3",
            "long_name4" to "4",
            "string_name5" to "5",
            "stringset_name6" to "68u^K>LK*O478u^K>LK*O48"
        )

        val prefNameEncrypter = mockk<Encrypter<String>>()

        val prefNameDecrypter = mockk<Decrypter<String>>()
        every { prefNameDecrypter.decrypt(any()) } answers { firstArg() }

        val prefValueEncrypter = mockk<Encrypter<String>>()

        val prefValueDecrypter = mockk<Decrypter<String>>()
        every { prefValueDecrypter.decrypt(any()) } answers { firstArg() }

        val sharedPrefs = EncryptableSharedPreferences(preferences, prefNameEncrypter, prefNameDecrypter, prefValueEncrypter, prefValueDecrypter)
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
        val preferences = mockk<SharedPreferences>()
        every { preferences.getString(any(), any()) } answers { secondArg() }
        every { preferences.getString("encrypted_boolean_name", any()) } answers { "encrypted_boolean_value" }

        val prefNameEncrypter = mockk<Encrypter<String>>()
        every { prefNameEncrypter.encrypt(any()) } answers { "encrypted_" + firstArg() }

        val prefNameDecrypter = mockk<Decrypter<String>>()

        val prefValueEncrypter = mockk<Encrypter<String>>()

        val prefValueDecrypter = mockk<Decrypter<String>>()
        every { prefValueDecrypter.decrypt("encrypted_boolean_value") } answers { "true" }

        val sharedPrefs = EncryptableSharedPreferences(preferences, prefNameEncrypter, prefNameDecrypter, prefValueEncrypter, prefValueDecrypter)
        val actual = sharedPrefs.getBoolean("name", false)

        assertThat(actual).isTrue()
    }

    @Test
    fun test_getBoolean_returns_defValue_when_name_not_exist() {
        val preferences = mockk<SharedPreferences>()
        every { preferences.getString(any(), any()) } answers { secondArg() }
        every { preferences.getString("encrypted_boolean_name", any()) } answers { "encrypted_boolean_value" }

        val prefNameEncrypter = mockk<Encrypter<String>>()
        every { prefNameEncrypter.encrypt(any()) } answers { "encrypted_" + firstArg() }

        val prefNameDecrypter = mockk<Decrypter<String>>()

        val prefValueEncrypter = mockk<Encrypter<String>>()

        val prefValueDecrypter = mockk<Decrypter<String>>()
        every { prefValueDecrypter.decrypt("encrypted_boolean_value") } answers { "true" }

        val sharedPrefs = EncryptableSharedPreferences(preferences, prefNameEncrypter, prefNameDecrypter, prefValueEncrypter, prefValueDecrypter)
        val actual = sharedPrefs.getBoolean("name_not_exist", false)

        assertThat(actual).isFalse()
    }

    @Test
    fun test_getFloat_returns_stored_value_when_name_exist() {
        val preferences = mockk<SharedPreferences>()
        every { preferences.getString(any(), any()) } answers { secondArg() }
        every { preferences.getString("encrypted_float_name", any()) } answers { "encrypted_float_value" }

        val prefNameEncrypter = mockk<Encrypter<String>>()
        every { prefNameEncrypter.encrypt(any()) } answers { "encrypted_" + firstArg() }

        val prefNameDecrypter = mockk<Decrypter<String>>()

        val prefValueEncrypter = mockk<Encrypter<String>>()

        val prefValueDecrypter = mockk<Decrypter<String>>()
        every { prefValueDecrypter.decrypt("encrypted_float_value") } answers { "1.0" }

        val sharedPrefs = EncryptableSharedPreferences(preferences, prefNameEncrypter, prefNameDecrypter, prefValueEncrypter, prefValueDecrypter)
        val actual = sharedPrefs.getFloat("name", 0f)

        assertThat(actual).isEqualTo(1.0f)
    }

    @Test
    fun test_getFloat_returns_defValue_when_name_not_exist() {
        val preferences = mockk<SharedPreferences>()
        every { preferences.getString(any(), any()) } answers { secondArg() }
        every { preferences.getString("encrypted_float_name", any()) } answers { "encrypted_float_value" }

        val prefNameEncrypter = mockk<Encrypter<String>>()
        every { prefNameEncrypter.encrypt(any()) } answers { "encrypted_" + firstArg() }

        val prefNameDecrypter = mockk<Decrypter<String>>()

        val prefValueEncrypter = mockk<Encrypter<String>>()

        val prefValueDecrypter = mockk<Decrypter<String>>()
        every { prefValueDecrypter.decrypt("encrypted_float_value") } answers { "1.0" }

        val sharedPrefs = EncryptableSharedPreferences(preferences, prefNameEncrypter, prefNameDecrypter, prefValueEncrypter, prefValueDecrypter)
        val actual = sharedPrefs.getFloat("name_not_exist", 0f)

        assertThat(actual).isEqualTo(0.0f)
    }

    @Test
    fun test_getInt_returns_stored_value_when_name_exist() {
        val preferences = mockk<SharedPreferences>()
        every { preferences.getString(any(), any()) } answers { secondArg() }
        every { preferences.getString("encrypted_int_name", any()) } answers { "encrypted_int_value" }

        val prefNameEncrypter = mockk<Encrypter<String>>()
        every { prefNameEncrypter.encrypt(any()) } answers { "encrypted_" + firstArg() }

        val prefNameDecrypter = mockk<Decrypter<String>>()

        val prefValueEncrypter = mockk<Encrypter<String>>()

        val prefValueDecrypter = mockk<Decrypter<String>>()
        every { prefValueDecrypter.decrypt("encrypted_int_value") } answers { "1" }

        val sharedPrefs = EncryptableSharedPreferences(preferences, prefNameEncrypter, prefNameDecrypter, prefValueEncrypter, prefValueDecrypter)
        val actual = sharedPrefs.getInt("name", 0)

        assertThat(actual).isEqualTo(1)
    }

    @Test
    fun test_getInt_returns_defValue_when_name_not_exist() {
        val preferences = mockk<SharedPreferences>()
        every { preferences.getString(any(), any()) } answers { secondArg() }
        every { preferences.getString("encrypted_int_name", any()) } answers { "encrypted_int_value" }

        val prefNameEncrypter = mockk<Encrypter<String>>()
        every { prefNameEncrypter.encrypt(any()) } answers { "encrypted_" + firstArg() }

        val prefNameDecrypter = mockk<Decrypter<String>>()

        val prefValueEncrypter = mockk<Encrypter<String>>()

        val prefValueDecrypter = mockk<Decrypter<String>>()
        every { prefValueDecrypter.decrypt("encrypted_int_value") } answers { "1" }

        val sharedPrefs = EncryptableSharedPreferences(preferences, prefNameEncrypter, prefNameDecrypter, prefValueEncrypter, prefValueDecrypter)
        val actual = sharedPrefs.getInt("name_not_exist", 0)

        assertThat(actual).isEqualTo(0)
    }

    @Test
    fun test_getLong_returns_stored_value_when_name_exist() {
        val preferences = mockk<SharedPreferences>()
        every { preferences.getString(any(), any()) } answers { secondArg() }
        every { preferences.getString("encrypted_long_name", any()) } answers { "encrypted_long_value" }

        val prefNameEncrypter = mockk<Encrypter<String>>()
        every { prefNameEncrypter.encrypt(any()) } answers { "encrypted_" + firstArg() }

        val prefNameDecrypter = mockk<Decrypter<String>>()

        val prefValueEncrypter = mockk<Encrypter<String>>()

        val prefValueDecrypter = mockk<Decrypter<String>>()
        every { prefValueDecrypter.decrypt("encrypted_long_value") } answers { "1" }

        val sharedPrefs = EncryptableSharedPreferences(preferences, prefNameEncrypter, prefNameDecrypter, prefValueEncrypter, prefValueDecrypter)
        val actual = sharedPrefs.getLong("name", 0L)

        assertThat(actual).isEqualTo(1)
    }

    @Test
    fun test_getLong_returns_defValue_when_name_not_exist() {
        val preferences = mockk<SharedPreferences>()
        every { preferences.getString(any(), any()) } answers { secondArg() }
        every { preferences.getString("encrypted_long_name", any()) } answers { "encrypted_long_value" }

        val prefNameEncrypter = mockk<Encrypter<String>>()
        every { prefNameEncrypter.encrypt(any()) } answers { "encrypted_" + firstArg() }

        val prefNameDecrypter = mockk<Decrypter<String>>()

        val prefValueEncrypter = mockk<Encrypter<String>>()

        val prefValueDecrypter = mockk<Decrypter<String>>()
        every { prefValueDecrypter.decrypt("encrypted_long_value") } answers { "1" }

        val sharedPrefs = EncryptableSharedPreferences(preferences, prefNameEncrypter, prefNameDecrypter, prefValueEncrypter, prefValueDecrypter)
        val actual = sharedPrefs.getLong("name_not_exist", 0L)

        assertThat(actual).isEqualTo(0L)
    }

    @Test
    fun test_getString_stored_value_when_name_exist() {
        val preferences = mockk<SharedPreferences>()
        every { preferences.getString(any(), any()) } answers { secondArg() }
        every { preferences.getString("encrypted_string_name", any()) } answers { "encrypted_string_value" }

        val prefNameEncrypter = mockk<Encrypter<String>>()
        every { prefNameEncrypter.encrypt(any()) } answers { "encrypted_" + firstArg() }

        val prefNameDecrypter = mockk<Decrypter<String>>()

        val prefValueEncrypter = mockk<Encrypter<String>>()

        val prefValueDecrypter = mockk<Decrypter<String>>()
        every { prefValueDecrypter.decrypt("encrypted_string_value") } answers { "string_value" }

        val sharedPrefs = EncryptableSharedPreferences(preferences, prefNameEncrypter, prefNameDecrypter, prefValueEncrypter, prefValueDecrypter)
        val actual = sharedPrefs.getString("name", "defValue")

        assertThat(actual).isEqualTo("string_value")
    }

    @Test
    fun test_getString_defValue_when_name_not_exist() {
        val preferences = mockk<SharedPreferences>()
        every { preferences.getString(any(), any()) } answers { secondArg() }
        every { preferences.getString("encrypted_string_name", any()) } answers { "encrypted_string_value" }

        val prefNameEncrypter = mockk<Encrypter<String>>()
        every { prefNameEncrypter.encrypt(any()) } answers { "encrypted_" + firstArg() }

        val prefNameDecrypter = mockk<Decrypter<String>>()

        val prefValueEncrypter = mockk<Encrypter<String>>()

        val prefValueDecrypter = mockk<Decrypter<String>>()
        every { prefValueDecrypter.decrypt("encrypted_string_value") } answers { "string_value" }

        val sharedPrefs = EncryptableSharedPreferences(preferences, prefNameEncrypter, prefNameDecrypter, prefValueEncrypter, prefValueDecrypter)
        val actual = sharedPrefs.getString("name_not_exist", null)

        assertThat(actual).isEqualTo(null)
    }

    @Test
    fun test_getStringSet_stored_value_when_name_exist() {
        val preferences = mockk<SharedPreferences>()
        every { preferences.getString(any(), any()) } answers { secondArg() }
        every { preferences.getString("encrypted_stringset_name", any()) } answers { "encrypted_stringset_value" }

        val prefNameEncrypter = mockk<Encrypter<String>>()
        every { prefNameEncrypter.encrypt(any()) } answers { "encrypted_" + firstArg() }

        val prefNameDecrypter = mockk<Decrypter<String>>()

        val prefValueEncrypter = mockk<Encrypter<String>>()

        val prefValueDecrypter = mockk<Decrypter<String>>()
        every { prefValueDecrypter.decrypt("encrypted_stringset_value") } answers { "18u^K>LK*O428u^K>LK*O43" }

        val sharedPrefs = EncryptableSharedPreferences(preferences, prefNameEncrypter, prefNameDecrypter, prefValueEncrypter, prefValueDecrypter)
        val actual = sharedPrefs.getStringSet("name", null)

        assertThat(actual).isEqualTo(mutableSetOf("1", "2", "3"))
    }

    @Test
    fun test_getStringSet_defValue_when_name_not_exist() {
        val preferences = mockk<SharedPreferences>()
        every { preferences.getString(any(), any()) } answers { secondArg() }
        every { preferences.getString("encrypted_stringset_name", any()) } answers { "encrypted_stringset_value" }

        val prefNameEncrypter = mockk<Encrypter<String>>()
        every { prefNameEncrypter.encrypt(any()) } answers { "encrypted_" + firstArg() }

        val prefNameDecrypter = mockk<Decrypter<String>>()

        val prefValueEncrypter = mockk<Encrypter<String>>()

        val prefValueDecrypter = mockk<Decrypter<String>>()
        every { prefValueDecrypter.decrypt("encrypted_stringset_value") } answers { "1;2;3" }

        val sharedPrefs = EncryptableSharedPreferences(preferences, prefNameEncrypter, prefNameDecrypter, prefValueEncrypter, prefValueDecrypter)
        val actual = sharedPrefs.getStringSet("name_not_exist", null)

        assertThat(actual).isEqualTo(null)
    }

    @Test
    fun test_registerOnSharedPreferenceChangeListener() {
        // Problem(2020.04.27): Mockk doesn't support private property mocks
        // See https://github.com/mockk/mockk/issues/104
        // val encrypter = mockk<Encrypter>()
        //
        // val preferences = mockk<SharedPreferences>()
        // every { preferences.registerOnSharedPreferenceChangeListener(any()) } just Runs
        //
        // val listenerMaps = mutableMapOf<SharedPreferences.OnSharedPreferenceChangeListener, SharedPreferences.OnSharedPreferenceChangeListener>()
        //
        // val comp = spyk(SymmetricKeyEncryptedSharedPreferences.Companion, recordPrivateCalls = true)
        // every { comp getProperty "listenerMap" } propertyType listenerMaps::class returns listenerMaps
        //
        // val sharedPrefs = SymmetricKeyEncryptedSharedPreferences(encrypter, preferences)
        // sharedPrefs.registerOnSharedPreferenceChangeListener { _, _ -> }
        //
        // assertThat(listenerMaps).isNotEmpty()
    }

    @Test
    fun test_unregisterOnSharedPreferenceChangeListener() {
        // Problem(2020.04.27): Mockk doesn't support private property mocks
        // See https://github.com/mockk/mockk/issues/104
    }
}

@RunWith(AndroidJUnit4::class)
class EditorImplTest {
    @Test
    fun test_apply_is_transparently() {
        val prefNameEncrypter = mockk<Encrypter<String>>()

        val prefValueEncrypter = mockk<Encrypter<String>>()

        val nativeEditor = mockk<SharedPreferences.Editor>()
        every { nativeEditor.apply() } just Runs

        val editor = EncryptableSharedPreferences.EditorImpl(nativeEditor, prefNameEncrypter, prefValueEncrypter)
        editor.apply()

        verify(exactly = 1) { nativeEditor.apply() }
    }

    @Test
    fun test_clear_is_transparently() {
        val prefNameEncrypter = mockk<Encrypter<String>>()

        val prefValueEncrypter = mockk<Encrypter<String>>()

        val nativeEditor = mockk<SharedPreferences.Editor>()
        every { nativeEditor.clear() } returns nativeEditor

        val editor = EncryptableSharedPreferences.EditorImpl(nativeEditor, prefNameEncrypter, prefValueEncrypter)
        val actual = editor.clear()

        assertThat(actual).isEqualTo(editor)

        verify(exactly = 1) { nativeEditor.clear() }
    }

    @Test
    fun test_commit_is_transparently() {
        val prefNameEncrypter = mockk<Encrypter<String>>()

        val prefValueEncrypter = mockk<Encrypter<String>>()

        val nativeEditor = mockk<SharedPreferences.Editor>()
        every { nativeEditor.commit() } returns true

        val editor = EncryptableSharedPreferences.EditorImpl(nativeEditor, prefNameEncrypter, prefValueEncrypter)
        val actual = editor.commit()

        verify { nativeEditor.commit() }
        assertThat(actual).isTrue()
    }

    @Test
    fun test_putBoolean_put_encrypted_value_into_preferences_if_input_is_nonnull() {
        val prefNameEncrypter = mockk<Encrypter<String>>()
        every { prefNameEncrypter.encrypt(any()) } answers { "encrypted_" + firstArg() }

        val prefValueEncrypter = mockk<Encrypter<String>>()
        every { prefValueEncrypter.encrypt(any()) } answers { "encrypted_" + firstArg() }

        val nativeEditor = mockk<SharedPreferences.Editor>()
        every { nativeEditor.putString(any(), any()) } returns nativeEditor

        val editor = EncryptableSharedPreferences.EditorImpl(nativeEditor, prefNameEncrypter, prefValueEncrypter)
        val actual = editor.putBoolean("name", true)

        assertThat(actual).isEqualTo(editor)

        verifyAll {
            prefNameEncrypter.encrypt("boolean_name")
            prefValueEncrypter.encrypt("true")
            nativeEditor.putString("encrypted_boolean_name", "encrypted_true")
        }
    }

    @Test
    fun test_putFloat_put_encrypted_value_into_preferences_if_input_is_nonnull() {
        val prefNameEncrypter = mockk<Encrypter<String>>()
        every { prefNameEncrypter.encrypt(any()) } answers { "encrypted_" + firstArg() }

        val prefValueEncrypter = mockk<Encrypter<String>>()
        every { prefValueEncrypter.encrypt(any()) } answers { "encrypted_" + firstArg() }

        val nativeEditor = mockk<SharedPreferences.Editor>()
        every { nativeEditor.putString(any(), any()) } returns nativeEditor

        val editor = EncryptableSharedPreferences.EditorImpl(nativeEditor, prefNameEncrypter, prefValueEncrypter)
        val actual = editor.putFloat("name", 1.0f)

        assertThat(actual).isEqualTo(editor)

        verifyAll {
            prefNameEncrypter.encrypt("float_name")
            prefValueEncrypter.encrypt("1.0")
            nativeEditor.putString("encrypted_float_name", "encrypted_1.0")
        }
    }

    @Test
    fun test_putInt_put_encrypted_value_into_preferences_if_input_is_nonnull() {
        val prefNameEncrypter = mockk<Encrypter<String>>()
        every { prefNameEncrypter.encrypt(any()) } answers { "encrypted_" + firstArg() }

        val prefValueEncrypter = mockk<Encrypter<String>>()
        every { prefValueEncrypter.encrypt(any()) } answers { "encrypted_" + firstArg() }

        val nativeEditor = mockk<SharedPreferences.Editor>()
        every { nativeEditor.putString(any(), any()) } returns nativeEditor

        val editor = EncryptableSharedPreferences.EditorImpl(nativeEditor, prefNameEncrypter, prefValueEncrypter)
        val actual = editor.putInt("name", 1)

        assertThat(actual).isEqualTo(editor)

        verifyAll {
            prefNameEncrypter.encrypt("int_name")
            prefValueEncrypter.encrypt("1")
            nativeEditor.putString("encrypted_int_name", "encrypted_1")
        }
    }

    @Test
    fun test_putLong_put_encrypted_value_into_preferences_if_input_is_nonnull() {
        val prefNameEncrypter = mockk<Encrypter<String>>()
        every { prefNameEncrypter.encrypt(any()) } answers { "encrypted_" + firstArg() }

        val prefValueEncrypter = mockk<Encrypter<String>>()
        every { prefValueEncrypter.encrypt(any()) } answers { "encrypted_" + firstArg() }

        val nativeEditor = mockk<SharedPreferences.Editor>()
        every { nativeEditor.putString(any(), any()) } returns nativeEditor

        val editor = EncryptableSharedPreferences.EditorImpl(nativeEditor, prefNameEncrypter, prefValueEncrypter)
        val actual = editor.putLong("name", 1L)

        assertThat(actual).isEqualTo(editor)

        verifyAll {
            prefNameEncrypter.encrypt("long_name")
            prefValueEncrypter.encrypt("1")
            nativeEditor.putString("encrypted_long_name", "encrypted_1")
        }
    }

    @Test
    fun test_putString_put_encrypted_value_into_preferences_if_input_is_nonnull() {
        val prefNameEncrypter = mockk<Encrypter<String>>()
        every { prefNameEncrypter.encrypt(any()) } answers { "encrypted_" + firstArg() }

        val prefValueEncrypter = mockk<Encrypter<String>>()
        every { prefValueEncrypter.encrypt(any()) } answers { "encrypted_" + firstArg() }

        val nativeEditor = mockk<SharedPreferences.Editor>()
        every { nativeEditor.putString(any(), any()) } returns nativeEditor

        val editor = EncryptableSharedPreferences.EditorImpl(nativeEditor, prefNameEncrypter, prefValueEncrypter)
        val actual = editor.putString("name", "value")

        assertThat(actual).isEqualTo(editor)

        verifyAll {
            prefNameEncrypter.encrypt("string_name")
            prefValueEncrypter.encrypt("value")
            nativeEditor.putString("encrypted_string_name", "encrypted_value")
        }
    }

    @Test
    fun test_putString_put_null_into_preferences_if_input_is_null() {
        val prefNameEncrypter = mockk<Encrypter<String>>()
        every { prefNameEncrypter.encrypt(any()) } answers { "encrypted_" + firstArg() }

        val prefValueEncrypter = mockk<Encrypter<String>>()
        every { prefValueEncrypter.encrypt(any()) } answers { "encrypted_" + firstArg() }

        val nativeEditor = mockk<SharedPreferences.Editor>()
        every { nativeEditor.putString(any(), any()) } returns nativeEditor

        val editor = EncryptableSharedPreferences.EditorImpl(nativeEditor, prefNameEncrypter, prefValueEncrypter)
        val actual = editor.putString("name", null)

        assertThat(actual).isEqualTo(editor)

        verifyAll {
            prefNameEncrypter.encrypt("string_name")
            nativeEditor.putString("encrypted_string_name", null)
        }
    }

    @Test
    fun test_putStringSet_put_encrypted_value_into_preferences_if_input_is_not_empty() {
        val prefNameEncrypter = mockk<Encrypter<String>>()
        every { prefNameEncrypter.encrypt(any()) } answers { "encrypted_" + firstArg() }

        val prefValueEncrypter = mockk<Encrypter<String>>()
        every { prefValueEncrypter.encrypt(any()) } answers { "encrypted_" + firstArg() }

        val nativeEditor = mockk<SharedPreferences.Editor>()
        every { nativeEditor.putString(any(), any()) } returns nativeEditor

        val editor = EncryptableSharedPreferences.EditorImpl(nativeEditor, prefNameEncrypter, prefValueEncrypter)
        val actual = editor.putStringSet("name", mutableSetOf("1", "2", "3"))

        assertThat(actual).isEqualTo(editor)

        verifyAll {
            prefNameEncrypter.encrypt("stringset_name")
            prefValueEncrypter.encrypt("18u^K>LK*O428u^K>LK*O43")
            nativeEditor.putString("encrypted_stringset_name", "encrypted_18u^K>LK*O428u^K>LK*O43")
        }
    }

    @Test
    fun test_putStringSet_put_encrypted_value_into_preferences_if_input_is_empty() {
        val prefNameEncrypter = mockk<Encrypter<String>>()
        every { prefNameEncrypter.encrypt(any()) } answers { "encrypted_" + firstArg() }

        val prefValueEncrypter = mockk<Encrypter<String>>()
        every { prefValueEncrypter.encrypt(any()) } answers { "encrypted_" + firstArg() }

        val nativeEditor = mockk<SharedPreferences.Editor>()
        every { nativeEditor.putString(any(), any()) } returns nativeEditor

        val editor = EncryptableSharedPreferences.EditorImpl(nativeEditor, prefNameEncrypter, prefValueEncrypter)
        val actual = editor.putStringSet("name", mutableSetOf())

        assertThat(actual).isEqualTo(editor)

        verifyAll {
            prefNameEncrypter.encrypt("stringset_name")
            prefValueEncrypter.encrypt("")
            nativeEditor.putString("encrypted_stringset_name", "encrypted_")
        }
    }

    @Test
    fun test_putStringSet_put_null_into_preferences_if_input_is_null() {
        val prefNameEncrypter = mockk<Encrypter<String>>()
        every { prefNameEncrypter.encrypt(any()) } answers { "encrypted_" + firstArg() }

        val prefValueEncrypter = mockk<Encrypter<String>>()
        every { prefValueEncrypter.encrypt(any()) } answers { "encrypted_" + firstArg() }

        val nativeEditor = mockk<SharedPreferences.Editor>()
        every { nativeEditor.putString(any(), any()) } returns nativeEditor

        val editor = EncryptableSharedPreferences.EditorImpl(nativeEditor, prefNameEncrypter, prefValueEncrypter)
        val actual = editor.putStringSet("name", null)

        assertThat(actual).isEqualTo(editor)

        verifyAll {
            prefNameEncrypter.encrypt("stringset_name")
            nativeEditor.putString("encrypted_stringset_name", null)
        }
    }

    @Test
    fun test_remove_is_transparently() {
        val prefNameEncrypter = mockk<Encrypter<String>>()
        every { prefNameEncrypter.encrypt(any()) } answers { "encrypted_" + firstArg() }

        val prefValueEncrypter = mockk<Encrypter<String>>()
        every { prefValueEncrypter.encrypt(any()) } answers { "encrypted_" + firstArg() }

        val nativeEditor = mockk<SharedPreferences.Editor>()
        every { nativeEditor.remove(any()) } returns nativeEditor

        val name = "name"
        val editor = EncryptableSharedPreferences.EditorImpl(nativeEditor, prefNameEncrypter, prefValueEncrypter)
        val actual = editor.remove(name)

        assertThat(actual).isEqualTo(editor)

        verify {
            nativeEditor.remove("encrypted_boolean_name")
            nativeEditor.remove("encrypted_float_name")
            nativeEditor.remove("encrypted_int_name")
            nativeEditor.remove("encrypted_long_name")
            nativeEditor.remove("encrypted_string_name")
            nativeEditor.remove("encrypted_stringset_name")
        }
    }
}
