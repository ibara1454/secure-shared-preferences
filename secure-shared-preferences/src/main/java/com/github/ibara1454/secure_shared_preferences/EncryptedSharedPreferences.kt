package com.github.ibara1454.secure_shared_preferences

import android.content.SharedPreferences
import com.github.ibara1454.secure_shared_preferences.cipher.*
import java.util.concurrent.ConcurrentHashMap

/**
 * An implementation of [SharedPreferences] that encrypts keys and values.
 */
internal class EncryptedSharedPreferences(
    private val storage: SharedPreferences,
    private val encrypter: Encrypter<String>,
    decrypter: Decrypter<String>
): SharedPreferences {
    private val encrypt = encrypter::encrypt

    private val decrypt = decrypter::decrypt

    override fun contains(name: String?): Boolean {
        return storage.contains(name?.let(encrypt))
    }

    override fun edit(): SharedPreferences.Editor {
        return EditorImpl(storage.edit(), encrypter)
    }

    override fun getAll(): MutableMap<String, *> {
        val entries = storage.all.entries.map {
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

    override fun getBoolean(name: String?, defValue: Boolean): Boolean {
        val tName = name?.let { encrypt("boolean_$name") }
        val crypto = storage.getString(tName, null)
        return if (crypto == null) {
            defValue
        } else {
            decrypt(crypto).toBoolean()
        }
    }

    override fun getFloat(name: String?, defValue: Float): Float {
        val tName = name?.let { encrypt("float_$name") }
        val crypto = storage.getString(tName, null)
        return if (crypto == null) {
            defValue
        } else {
            decrypt(crypto).toFloat()
        }
    }

    override fun getInt(name: String?, defValue: Int): Int {
        val tName = name?.let { encrypt("int_$name") }
        val crypto = storage.getString(tName, null)
        return if (crypto == null) {
            defValue
        } else {
            decrypt(crypto).toInt()
        }
    }

    override fun getLong(name: String?, defValue: Long): Long {
        val tName = name?.let { encrypt("long_$name") }
        val crypto = storage.getString(tName, null)
        return if (crypto == null) {
            defValue
        } else {
            decrypt(crypto).toLong()
        }
    }

    override fun getString(name: String?, defValue: String?): String? {
        val tName = name?.let { encrypt("string_$name") }
        val crypto = storage.getString(tName, null)
        return if (crypto == null) {
            defValue
        } else {
            decrypt(crypto)
        }
    }

    override fun getStringSet(name: String?, defValue: MutableSet<String>?): MutableSet<String>? {
        val tName = name?.let { encrypt("stringset_$name") }
        val crypto = storage.getString(tName, null)
        return if (crypto == null) {
            defValue
        } else {
            val values = decrypt(crypto).split(";")
            values.toMutableSet()
        }
    }

    override fun registerOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener?) {
        if (listener != null) {
            val encryptListener = SharedPreferences.OnSharedPreferenceChangeListener { _, crypto ->
                val tName = decrypt(crypto)
                val name = tName.substringAfter("_")
                listener.onSharedPreferenceChanged(this, name)
            }
            listenerMap[listener] = encryptListener
            storage.registerOnSharedPreferenceChangeListener(encryptListener)
        }
    }

    override fun unregisterOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener?) {
        val encryptListener = listenerMap[listener]
        storage.unregisterOnSharedPreferenceChangeListener(encryptListener)
        listenerMap.remove(listener)
    }

    companion object {
        // Note: The initialization of companion object itself is thread-safe.
        // See: https://kotlinlang.org/docs/tutorials/kotlin-for-py/objects-and-companion-objects.html#companion-objects
        private val listenerMap: MutableMap<SharedPreferences.OnSharedPreferenceChangeListener, SharedPreferences.OnSharedPreferenceChangeListener>
            = ConcurrentHashMap()
    }

    internal class EditorImpl(
        private val editor: SharedPreferences.Editor,
        encrypter: Encrypter<String>
    ): SharedPreferences.Editor {
        private val encrypt = encrypter::encrypt

        override fun apply() {
            editor.apply()
        }

        override fun clear(): SharedPreferences.Editor {
            return editor.clear()
        }

        override fun commit(): Boolean {
            return editor.commit()
        }

        override fun putBoolean(name: String?, value: Boolean): SharedPreferences.Editor {
            return editor.putString(
                name?.let { encrypt("boolean_$name") } ,
                value.toString().let(encrypt)
            )
        }

        override fun putFloat(name: String?, value: Float): SharedPreferences.Editor {
            // TODO: use scientific notation to convert to string instead
            return editor.putString(
                name?.let { encrypt("float_$name") } ,
                value.toString().let(encrypt)
            )
        }

        override fun putInt(name: String?, value: Int): SharedPreferences.Editor {
            return editor.putString(
                name?.let { encrypt("int_$name") } ,
                value.toString().let(encrypt)
            )
        }

        override fun putLong(name: String?, value: Long): SharedPreferences.Editor {
            return editor.putString(
                name?.let { encrypt("long_$name") } ,
                value.toString().let(encrypt)
            )
        }

        override fun putString(name: String?, value: String?): SharedPreferences.Editor {
            return editor.putString(
                name?.let { encrypt("string_$name") } ,
                value?.let(encrypt)
            )
        }

        override fun putStringSet(name: String?, value: MutableSet<String>?): SharedPreferences.Editor {
            // FIXME: choose separator dependent on value dynamically
            val separator = ";"
            return editor.putString(
                name?.let { encrypt("stringset_$name") } ,
                value?.joinToString(separator = separator)?.let(encrypt)
            )
        }

        override fun remove(name: String?): SharedPreferences.Editor {
            return editor.remove(name?.let(encrypt))
        }
    }
}
