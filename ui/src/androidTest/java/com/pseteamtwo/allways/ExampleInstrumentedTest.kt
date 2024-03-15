package com.pseteamtwo.allways

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.fynng.allways.MainActivity
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    /*@Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.fynng.ui", appContext.packageName)
    }*/
    @JvmField
    @Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun testExample() {
        composeRule.onNodeWithText("Einloggen").assertIsDisplayed()
    }

    @Test
    fun testSetLoginFailed() {

    }
}