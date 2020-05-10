package com.github.ibara1454.secure_shared_preferences.shared_preferences

import android.content.SharedPreferences
import com.github.ibara1454.secure_shared_preferences.cipher.*
import java.util.concurrent.ConcurrentHashMap

/**
 * An implementation of [SharedPreferences] that encrypts keys and values.
 *
 * @param preferences any [SharedPreferences] used for save / read values.
 * @param encrypter an encrypter transforms plain text to encrypted text.
 * @param decrypter an decrypter transforms encrypted text to plain text.
 */
internal class EncryptableSharedPreferences(
    private val preferences: SharedPreferences,
    private val encrypter: Encrypter<String>,
    decrypter: Decrypter<String>
): SharedPreferences {
    private val encrypt = encrypter::encrypt

    private val decrypt = decrypter::decrypt

    /**
     * Checks whether the preferences contains a preference.
     *
     * @param key the name of the preference to check.
     * @return returns true if the preference exists in the preferences,
     *  otherwise false.
     */
    override fun contains(key: String?): Boolean {
        return preferences.contains(key?.let(encrypt))
    }

    /**
     * Create a new Editor for these preferences.
     * @return returns a new instance of the [SharedPreferences.Editor] interface, allowing
     * you to modify the values in this SharedPreferences object.
     */
    override fun edit(): SharedPreferences.Editor {
        return EditorImpl(preferences.edit(), encrypter)
    }

    /**
     * Retrieve all values from the preferences.
     * @return returns a map containing a list of pairs key/value representing
     * the preferences.
     */
    override fun getAll(): MutableMap<String, *> {
        val entries = preferences.all.entries.map {
            val tName = decrypt(it.key)
            val type = tName.substringBefore("_")
            val name = tName.substringAfter("_")
            val dValue = it.value as String
            val value: Any? =
                when (type) {
                    "boolean" -> decrypt(dValue).toBoolean()
                    "float" -> decrypt(dValue).toFloat()
                    "int" -> decrypt(dValue).toInt()
                    "long" -> decrypt(dValue).toLong()
                    "string" -> decrypt(dValue)
                    "stringset" -> decrypt(dValue).split(";").toMutableSet()
                    else -> null
                }
            name to value
        }.toTypedArray()
        return mutableMapOf(*entries)
    }

    /**
     * Retrieve a boolean value from the preferences.
     *
     * @param key the name of the preference to retrieve.
     * @param defValue value to return if this preference does not exist.
     * @return returns the preference value if it exists, or defValue.
     * @throws ClassCastException if there is a preference with this name that is not a boolean.
     */
    override fun getBoolean(key: String?, defValue: Boolean): Boolean {
        val tName = key?.let { encrypt("boolean_$key") }
        val crypto = preferences.getString(tName, null)
        return if (crypto == null) {
            defValue
        } else {
            decrypt(crypto).toBoolean()
        }
    }

    /**
     * Retrieve a float value from the preferences.
     *
     * @param key the name of the preference to retrieve.
     * @param defValue value to return if this preference does not exist.
     * @return returns the preference value if it exists, or defValue.
     * @throws ClassCastException if there is a preference with this name that is not an float.
     */
    override fun getFloat(key: String?, defValue: Float): Float {
        val tName = key?.let { encrypt("float_$key") }
        val crypto = preferences.getString(tName, null)
        return if (crypto == null) {
            defValue
        } else {
            decrypt(crypto).toFloat()
        }
    }

    /**
     * Retrieve an int value from the preferences.
     *
     * @param key the name of the preference to retrieve.
     * @param defValue value to return if this preference does not exist.
     * @return returns the preference value if it exists, or defValue.
     * @throws ClassCastException if there is a preference with this name that is not an int.
     */
    override fun getInt(key: String?, defValue: Int): Int {
        val tName = key?.let { encrypt("int_$key") }
        val crypto = preferences.getString(tName, null)
        return if (crypto == null) {
            defValue
        } else {
            decrypt(crypto).toInt()
        }
    }

    /**
     * Retrieve a long value from the preferences.
     *
     * @param key the name of the preference to retrieve.
     * @param defValue value to return if this preference does not exist.
     * @return returns the preference value if it exists, or defValue.
     * @throws ClassCastException if there is a preference with this name that is not a long.
     */
    override fun getLong(key: String?, defValue: Long): Long {
        val tName = key?.let { encrypt("long_$key") }
        val crypto = preferences.getString(tName, null)
        return if (crypto == null) {
            defValue
        } else {
            decrypt(crypto).toLong()
        }
    }

    /**
     * Retrieve a String value from the preferences.
     *
     * @param key the name of the preference to retrieve.
     * @param defValue value to return if this preference does not exist.
     * @return returns the preference value if it exists, or defValue.
     * @throws ClassCastException if there is a preference with this name that is not a String.
     */
    override fun getString(key: String?, defValue: String?): String? {
        val tName = key?.let { encrypt("string_$key") }
        val crypto = preferences.getString(tName, null)
        return if (crypto == null) {
            defValue
        } else {
            decrypt(crypto)
        }
    }

    /**
     * Retrieve a set of String values from the preferences.
     *
     * @param key the name of the preference to retrieve.
     * @param defValues values to return if this preference does not exist.
     * @return returns the preference values if they exist, or defValues.
     * @throws ClassCastException if there is a preference with this name that is not a Set.
     */
    override fun getStringSet(key: String?, defValues: MutableSet<String>?): MutableSet<String>? {
        val tName = key?.let { encrypt("stringset_$key") }
        val crypto = preferences.getString(tName, null)
        return if (crypto == null) {
            defValues
        } else {
            val values = decrypt(crypto).split(";")
            values.toMutableSet()
        }
    }

    /**
     * Registers a callback to be invoked when a change happens to a preference.
     *
     * @param listener the callback will triggered on data changes.
     * @see [unregisterOnSharedPreferenceChangeListener]
     */
    override fun registerOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener?) {
        if (listener != null) {
            val encryptListener = SharedPreferences.OnSharedPreferenceChangeListener { _, crypto ->
                val tName = decrypt(crypto)
                val name = tName.substringAfter("_")
                listener.onSharedPreferenceChanged(this, name)
            }
            listenerMap[listener] = encryptListener
            preferences.registerOnSharedPreferenceChangeListener(encryptListener)
        }
    }

    /**
     * Unregisters a previous callback.
     *
     * @param listener the callback that should be unregistered.
     * @see [registerOnSharedPreferenceChangeListener]
     */
    override fun unregisterOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener?) {
        val encryptListener = listenerMap[listener]
        preferences.unregisterOnSharedPreferenceChangeListener(encryptListener)
        listenerMap.remove(listener)
    }

    companion object {
        // Note: The initialization of companion object itself is thread-safe.
        // See: https://kotlinlang.org/docs/tutorials/kotlin-for-py/objects-and-companion-objects.html#companion-objects
        private val listenerMap: MutableMap<SharedPreferences.OnSharedPreferenceChangeListener, SharedPreferences.OnSharedPreferenceChangeListener>
            = ConcurrentHashMap()
    }

    /**
     * The editor implementation for [EncryptableSharedPreferences].
     *
     * @param editor the editor received from [preferences].
     * @param encrypter an encrypter transforms plain text to encrypted text.
     */
    internal class EditorImpl(
        private val editor: SharedPreferences.Editor,
        encrypter: Encrypter<String>
    ): SharedPreferences.Editor {
        private val encrypt = encrypter::encrypt

        /**
         * Commit your preferences changes back from this Editor to the
         * [SharedPreferences] object it is editing. This atomically
         * performs the requested modifications, replacing whatever is currently
         * in the SharedPreferences.
         */
        override fun apply() {
            editor.apply()
        }

        /**
         * Mark in the editor to remove *all* values from the
         * preferences.  Once commit is called, the only remaining preferences
         * will be any that you have defined in this editor.
         */
        override fun clear(): SharedPreferences.Editor {
            return editor.clear()
        }

        /**
         * Commit your preferences changes back from this Editor to the
         * [SharedPreferences] object it is editing.  This atomically
         * performs the requested modifications, replacing whatever is currently
         * in the SharedPreferences.
         */
        override fun commit(): Boolean {
            return editor.commit()
        }

        /**
         * Set a boolean value in the preferences editor, to be written back once
         * [commit] or [apply] are called.
         *
         * @param key the name of the preference to modify.
         * @param value the new value for the preference.
         * @return returns a reference to the same Editor object, so you can
         * chain put calls together.
         */
        override fun putBoolean(key: String?, value: Boolean): SharedPreferences.Editor {
            return editor.putString(
                key?.let { encrypt("boolean_$key") } ,
                value.toString().let(encrypt)
            )
        }

        /**
         * Set a float value in the preferences editor, to be written back once
         * [commit] or [apply] are called.
         *
         * @param key the name of the preference to modify.
         * @param value the new value for the preference.
         *
         * @return returns a reference to the same Editor object, so you can
         * chain put calls together.
         */
        override fun putFloat(key: String?, value: Float): SharedPreferences.Editor {
            // TODO: use scientific notation to convert to string instead
            return editor.putString(
                key?.let { encrypt("float_$key") } ,
                value.toString().let(encrypt)
            )
        }

        /**
         * Set an int value in the preferences editor, to be written back once
         * [commit] or [apply] are called.
         *
         * @param key the name of the preference to modify.
         * @param value the new value for the preference.
         * @return returns a reference to the same Editor object, so you can
         *  chain put calls together.
         */
        override fun putInt(key: String?, value: Int): SharedPreferences.Editor {
            return editor.putString(
                key?.let { encrypt("int_$key") } ,
                value.toString().let(encrypt)
            )
        }

        /**
         * Set a long value in the preferences editor, to be written back once
         * [commit] or [apply] are called.
         *
         * @param key the name of the preference to modify.
         * @param value the new value for the preference.
         * @return returns a reference to the same Editor object, so you can
         *  chain put calls together.
         */
        override fun putLong(key: String?, value: Long): SharedPreferences.Editor {
            return editor.putString(
                key?.let { encrypt("long_$key") } ,
                value.toString().let(encrypt)
            )
        }

        /**
         * Set a String value in the preferences editor, to be written back once
         * [commit] or [apply] are called.
         *
         * @param key the name of the preference to modify.
         * @param value the new value for the preference. Passing `null`
         *  for this argument is equivalent to calling [remove] with
         *  this key.
         * @return returns a reference to the same Editor object, so you can
         *  chain put calls together.
         */
        override fun putString(key: String?, value: String?): SharedPreferences.Editor {
            return editor.putString(
                key?.let { encrypt("string_$key") } ,
                value?.let(encrypt)
            )
        }

        /**
         * Set a set of String values in the preferences editor, to be written
         * back once [commit] or [apply] is called.
         *
         * @param key the name of the preference to modify.
         * @param values the set of new values for the preference.  Passing `null`
         *  for this argument is equivalent to calling [remove] with
         *  this key.
         * @return returns a reference to the same Editor object, so you can
         * chain put calls together.
         */
        override fun putStringSet(key: String?, values: MutableSet<String>?): SharedPreferences.Editor {
            // FIXME: choose separator dependent on value dynamically
            val separator = ";"
            return editor.putString(
                key?.let { encrypt("stringset_$key") } ,
                values?.joinToString(separator = separator)?.let(encrypt)
            )
        }

        /**
         * Mark in the editor that a preference value should be removed, which
         * will be done in the actual preferences once [commit] is
         * called.
         *
         * @param key the name of the preference to remove.
         * @return returns a reference to the same Editor object, so you can
         * chain put calls together.
         */
        override fun remove(key: String?): SharedPreferences.Editor {
            // FIXME: this implementation does not work well
            if (key != null) {
                // Try remove all combinations of (type, key)
                listOf("boolean", "float", "int", "long", "string", "stringset")
                    .map { "${it}_${key}" }
                    .forEach { editor.remove(encrypt(it)) }
            }
            return editor
        }
    }
}
