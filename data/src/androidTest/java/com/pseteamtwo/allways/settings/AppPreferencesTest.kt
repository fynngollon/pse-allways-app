package com.pseteamtwo.allways.settings

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import junit.framework.TestCase.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AppPreferencesTest {

    private lateinit var appPreferences: AppPreferences
    private lateinit var context: Context

    @Before
    fun setUp() {
        context = InstrumentationRegistry.getInstrumentation().targetContext

        appPreferences = AppPreferences(
            context
        )
    }

    @Test
    fun isTrackingEnabled() {
        assertEquals(false, appPreferences.isTrackingEnabled)

        appPreferences.isTrackingEnabled = true

        assertEquals(true, appPreferences.isTrackingEnabled)
    }
}