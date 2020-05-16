package com.github.ibara1454.sample

import android.app.Application
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.github.ibara1454.secure_shared_preferences.getSecureSharedPreferences
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.Locale

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val preferences = getApplication<Application>()
        .getSecureSharedPreferences("name", AppCompatActivity.MODE_PRIVATE)

    val countBooleanLiveData: LiveData<String> =
        SharedPreferenceBooleanLiveData(
            preferences,
            "count_boolean",
            true
        )
        .map { it.toString() }

    val countLongLiveData: LiveData<String> =
        SharedPreferenceLongLiveData(
            preferences,
            "count_long",
            0L
        )
        .map { it.toString() }

    val countIntLiveData: LiveData<String> =
        SharedPreferenceIntLiveData(
            preferences,
            "count_int",
            0
        )
        .map { it.toString() }

    val countFloatLiveData: LiveData<String> =
        SharedPreferenceFloatLiveData(
            preferences,
            "count_float",
            0.0f
        )
        .map { it.toString() }

    val countStringLiveData: LiveData<String> =
        SharedPreferenceStringLiveData(
            preferences,
            "count_string",
            "0"
        )
        .map { it ?: "null" }

    val countStringSetLiveData: LiveData<String> =
        SharedPreferenceStringSetLiveData(
            preferences,
            "count_string_set",
            setOf("a", "b", "c")
        )
        .map { it?.joinToString(",") ?: "null" }

    private val allChannel: ConflatedBroadcastChannel<Map<String, *>> = ConflatedBroadcastChannel()

    val allLiveData: LiveData<String> = allChannel.asFlow()
        .map { m ->
            m.map { "${it.key} = ${it.value?.toString() ?: "null"}" }
                .joinToString(",\n")
        }
        .asLiveData()

    init {
        viewModelScope.launch {
            while (true) {
                val countBoolean = preferences.getBoolean("count_boolean", true)
                preferences.edit().putBoolean("count_boolean", countBoolean xor true).apply()

                val countInt = preferences.getInt("count_int", 0)
                preferences.edit().putInt("count_int", countInt + 1).apply()

                val countLong = preferences.getLong("count_long", 0L)
                preferences.edit().putLong("count_long", countLong + 1L).apply()

                val countFloat = preferences.getFloat("count_float", 0.0f)
                preferences.edit().putFloat("count_float", countFloat + 1.0f).apply()

                val countString = preferences.getString("count_string", "0")!!
                preferences.edit().putString("count_string", "${countString.toInt() + 1}").apply()

                val countStringSet = preferences.getStringSet(
                    "count_string_set",
                    setOf("a", "b", "c")
                )!!
                preferences.edit().putStringSet("count_string_set", countStringSet.map {
                    if (it[0].isUpperCase()) {
                        it.toLowerCase(Locale.US)
                    } else {
                        it.toUpperCase(Locale.US)
                    }
                }.toSet()).apply()

                allChannel.offer(preferences.all)

                delay(1_000L)
            }
        }
    }
}

abstract class SharedPreferenceLiveData<T>(
    val sharedPrefs: SharedPreferences,
    private val key: String,
    private val defValue: T
) : LiveData<T>() {

    private val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
        if (key == this.key) {
            value = getValueFromPreferences(key, defValue)
        }
    }

    abstract fun getValueFromPreferences(key: String, defValue: T): T

    override fun onActive() {
        super.onActive()
        value = getValueFromPreferences(key, defValue)
        sharedPrefs.registerOnSharedPreferenceChangeListener(listener)
    }

    override fun onInactive() {
        sharedPrefs.unregisterOnSharedPreferenceChangeListener(listener)
        super.onInactive()
    }
}

class SharedPreferenceBooleanLiveData(
    sharedPrefs: SharedPreferences,
    key: String,
    defValue: Boolean
) :
    SharedPreferenceLiveData<Boolean>(sharedPrefs, key, defValue) {
    override fun getValueFromPreferences(key: String, defValue: Boolean): Boolean =
        sharedPrefs.getBoolean(key, defValue)
}

class SharedPreferenceIntLiveData(sharedPrefs: SharedPreferences, key: String, defValue: Int) :
    SharedPreferenceLiveData<Int>(sharedPrefs, key, defValue) {
    override fun getValueFromPreferences(key: String, defValue: Int): Int =
        sharedPrefs.getInt(key, defValue)
}

class SharedPreferenceLongLiveData(sharedPrefs: SharedPreferences, key: String, defValue: Long) :
    SharedPreferenceLiveData<Long>(sharedPrefs, key, defValue) {
    override fun getValueFromPreferences(key: String, defValue: Long): Long =
        sharedPrefs.getLong(key, defValue)
}

class SharedPreferenceFloatLiveData(sharedPrefs: SharedPreferences, key: String, defValue: Float) :
    SharedPreferenceLiveData<Float>(sharedPrefs, key, defValue) {
    override fun getValueFromPreferences(key: String, defValue: Float): Float =
        sharedPrefs.getFloat(key, defValue)
}

class SharedPreferenceStringLiveData(
    sharedPrefs: SharedPreferences,
    key: String,
    defValue: String?
) :
    SharedPreferenceLiveData<String?>(sharedPrefs, key, defValue) {
    override fun getValueFromPreferences(key: String, defValue: String?): String? =
        sharedPrefs.getString(key, defValue)
}

class SharedPreferenceStringSetLiveData(
    sharedPrefs: SharedPreferences,
    key: String,
    defValue: Set<String>?
) :
    SharedPreferenceLiveData<Set<String>?>(sharedPrefs, key, defValue) {
    override fun getValueFromPreferences(key: String, defValue: Set<String>?): Set<String>? =
        sharedPrefs.getStringSet(key, defValue)
}
