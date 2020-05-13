package com.github.ibara1454.secure_shared_preferences.shared_preferences

import android.content.SharedPreferences
import com.github.ibara1454.secure_shared_preferences.cipher.*
import java.util.concurrent.ConcurrentHashMap

/**
 * An implementation of [SharedPreferences] that encrypts keys and values.
 *
 * @param preferences any [SharedPreferences] used for save / read values.
 * @param prefValueEncrypter an encrypter transforms plain text to encrypted text.
 * @param prefValueDecrypter an decrypter transforms encrypted text to plain text.
 */
internal class EncryptableSharedPreferences(
    private val preferences: SharedPreferences,
    private val prefNameEncrypter: Encrypter<String>,
    prefNameDecrypter: Decrypter<String>,
    private val prefValueEncrypter: Encrypter<String>,
    prefValueDecrypter: Decrypter<String>
): SharedPreferences {
    private val pnEncrypt = prefNameEncrypter::encrypt

    private val pnDecrypt = prefNameDecrypter::decrypt

    private val pvEncrypt = prefValueEncrypter::encrypt

    private val pvDecrypt = prefValueDecrypter::decrypt

    /**
     * Checks whether the preferences contains a preference.
     *
     * @param key the name of the preference to check.
     * @return returns true if the preference exists in the preferences,
     *  otherwise false.
     */
    override fun contains(key: String?): Boolean {
        return preferences.contains(key?.let(pnEncrypt))
    }

    /**
     * Create a new Editor for these preferences.
     * @return returns a new instance of the [SharedPreferences.Editor] interface, allowing
     * you to modify the values in this SharedPreferences object.
     */
    override fun edit(): SharedPreferences.Editor {
        return EditorImpl(preferences.edit(), prefNameEncrypter, prefValueEncrypter)
    }

    /**
     * Retrieve all values from the preferences.
     * @return returns a map containing a list of pairs key/value representing
     * the preferences.
     */
    override fun getAll(): MutableMap<String, *> {
        val entries = preferences.all.entries.map {
            val tName = pnDecrypt(it.key)
            val type = tName.substringBefore("_")
            val name = tName.substringAfter("_")
            val dValue = it.value as String
            val value: Any? =
                when (type) {
                    "boolean" -> pvDecrypt(dValue).toBoolean()
                    "float" -> pvDecrypt(dValue).toFloat()
                    "int" -> pvDecrypt(dValue).toInt()
                    "long" -> pvDecrypt(dValue).toLong()
                    "string" -> pvDecrypt(dValue)
                    "stringset" -> pvDecrypt(dValue).split("8u^K>LK*O4").toMutableSet()
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
        val tName = key?.let { pnEncrypt("boolean_$key") }
        val crypto = preferences.getString(tName, null)
        return if (crypto == null) {
            defValue
        } else {
            pvDecrypt(crypto).toBoolean()
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
        val tName = key?.let { pnEncrypt("float_$key") }
        val crypto = preferences.getString(tName, null)
        return if (crypto == null) {
            defValue
        } else {
            pvDecrypt(crypto).toFloat()
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
        val tName = key?.let { pnEncrypt("int_$key") }
        val crypto = preferences.getString(tName, null)
        return if (crypto == null) {
            defValue
        } else {
            pvDecrypt(crypto).toInt()
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
        val tName = key?.let { pnEncrypt("long_$key") }
        val crypto = preferences.getString(tName, null)
        return if (crypto == null) {
            defValue
        } else {
            pvDecrypt(crypto).toLong()
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
        val tName = key?.let { pnEncrypt("string_$key") }
        val crypto = preferences.getString(tName, null)
        return if (crypto == null) {
            defValue
        } else {
            pvDecrypt(crypto)
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
        val tName = key?.let { pnEncrypt("stringset_$key") }
        val crypto = preferences.getString(tName, null)
        return if (crypto == null) {
            defValues
        } else {
            // An random generate string.
            // The random string is complex enough so it would not disturb the given data.
            val delimiter = "8u^K>LK*O4"
            val values = pvDecrypt(crypto).split(delimiter)
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
                val tName = pnDecrypt(crypto)
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
     * @param prefNameEncrypter an encrypter transforms plain text to encrypted text.
     * @param prefValueEncrypter an encrypter transforms plain text to encrypted text.
     */
    internal class EditorImpl(
        private val editor: SharedPreferences.Editor,
        prefNameEncrypter: Encrypter<String>,
        prefValueEncrypter: Encrypter<String>
    ): SharedPreferences.Editor {
        private val pnEncrypt = prefNameEncrypter::encrypt

        private val pvEncrypt = prefValueEncrypter::encrypt

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
            editor.clear()
            return this
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
            editor.putString(
                key?.let { pnEncrypt("boolean_$key") } ,
                value.toString().let(pvEncrypt)
            )
            return this
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
            editor.putString(
                key?.let { pnEncrypt("float_$key") } ,
                value.toString().let(pvEncrypt)
            )
            return this
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
            editor.putString(
                key?.let { pnEncrypt("int_$key") } ,
                value.toString().let(pvEncrypt)
            )
            return this
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
            editor.putString(
                key?.let { pnEncrypt("long_$key") } ,
                value.toString().let(pvEncrypt)
            )
            return this
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
            editor.putString(
                key?.let { pnEncrypt("string_$key") } ,
                value?.let(pvEncrypt)
            )
            return this
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
            // An random generate string.
            // The random string is complex enough so it would not disturb the given data.
            val separator = "8u^K>LK*O4"
            editor.putString(
                key?.let { pnEncrypt("stringset_$key") } ,
                values?.joinToString(separator)?.let(pvEncrypt)
            )
            return this
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
                    .forEach { editor.remove(pnEncrypt(it)) }
            }
            return this
        }
    }
}
