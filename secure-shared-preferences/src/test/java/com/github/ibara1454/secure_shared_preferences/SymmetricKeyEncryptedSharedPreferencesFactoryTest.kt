package com.github.ibara1454.secure_shared_preferences

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SymmetricKeyEncryptedSharedPreferencesFactoryTest {
    // Context of the app under test.
    private val appContext = InstrumentationRegistry.getInstrumentation().targetContext

    @Test
    fun test() {
        SymmetricKeyEncryptedSharedPreferencesFactory(appContext)
    }
}
