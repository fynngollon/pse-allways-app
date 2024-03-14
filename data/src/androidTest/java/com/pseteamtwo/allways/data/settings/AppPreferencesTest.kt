package com.pseteamtwo.allways.data.settings

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import junit.framework.TestCase.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 * Especially needed to access [AppPreferences] and the batteryManager for the device the app
 * should run on to access the settings and current state of battery to modify those settings.
 *
 * This test tests [com.pseteamtwo.allways.settings.AppPreferences].
 */
@RunWith(AndroidJUnit4::class)
class AppPreferencesTest {
    //Test properties
    private val defaultLanguage = Language.GERMAN
    private val defaultIsTrackingEnabled = false
    private val defaultTrackingRegularity = TrackingRegularity.MEDIUM
    private val defaultIsBatteryDependencyEnabled = true
    private val defaultBatteryDependency = Pair(25, TrackingRegularity.RARELY)


    //Test dependencies
    private lateinit var context: Context

    //Class under test
    private lateinit var appPreferences: AppPreferences


    @Before
    fun setUp() {
        context = InstrumentationRegistry.getInstrumentation().targetContext

        appPreferences = AppPreferences(
            context
        )
    }


    @Test
    fun setAndGetLanguage() {
        assertEquals(defaultLanguage, appPreferences.language)
        appPreferences.language = Language.ENGLISH
        assertEquals(Language.ENGLISH, appPreferences.language)
    }

    @Test
    fun setAndGetIsTrackingEnabled() {
        assertEquals(defaultIsTrackingEnabled, appPreferences.isTrackingEnabled)
        appPreferences.isTrackingEnabled = true
        assertEquals(true, appPreferences.isTrackingEnabled)
    }

    @Test
    fun setAndGetTrackingRegularity() {
        assertEquals(defaultTrackingRegularity, appPreferences.trackingRegularity)
        appPreferences.trackingRegularity = TrackingRegularity.NEVER
        assertEquals(TrackingRegularity.NEVER, appPreferences.trackingRegularity)
    }

    @Test
    fun setAndGetIsBatteryDependencyEnabled() {
        assertEquals(defaultIsBatteryDependencyEnabled, appPreferences.isBatteryDependencyEnabled)
        appPreferences.isBatteryDependencyEnabled = false
        assertEquals(false, appPreferences.isBatteryDependencyEnabled)
    }

    @Test
    fun setAndGetBatteryDependency() {
        assertEquals(defaultBatteryDependency, appPreferences.batteryDependency)
        appPreferences.batteryDependency = Pair(10, TrackingRegularity.NEVER)
        assertEquals(Pair(10, TrackingRegularity.NEVER), appPreferences.batteryDependency)
    }

    @Test
    fun useBatteryDependencyToModifyTrackingRegularityToSwitchOffTracking() {
        assertEquals(defaultIsTrackingEnabled, appPreferences.isTrackingEnabled)
        assertEquals(defaultTrackingRegularity, appPreferences.trackingRegularity)
        //BatteryDependency should be enabled for this test
        appPreferences.isBatteryDependencyEnabled = true
        assertEquals(defaultBatteryDependency, appPreferences.batteryDependency)

        //Modify BatteryDependency in a way it will always trigger a change
        appPreferences.batteryDependency = Pair(100, TrackingRegularity.NEVER)

        //Check if that change effects TrackingRegularity and IsTrackingEnabled in the proper way
        assertEquals(TrackingRegularity.NEVER, appPreferences.trackingRegularity)
        assertEquals(false, appPreferences.isTrackingEnabled)
    }
}
