package com.pseteamtwo.allways.data.settings

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.BatteryManager
import com.pseteamtwo.allways.data.trip.tracking.ACTION_START
import com.pseteamtwo.allways.data.trip.tracking.ACTION_STOP
import com.pseteamtwo.allways.data.trip.tracking.hasLocationPermission
import com.pseteamtwo.allways.data.trip.tracking.location.LocationService
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton


/**
 * This class defines and enables editing of the settings for this application.
 * This is accomplished by [SharedPreferences], which hold the settings.
 * As [SharedPreferences] can only store primitive data types, complex types are serialized
 * through [kotlinx.serialization] to a string representation.
 * This class follows the singleton-pattern.
 *
 * @property context Enables access to resources and services of the system.
 * @constructor Creates an instance of this class.
 */
@Singleton
class AppPreferences @Inject constructor(
    @ApplicationContext val context: Context
) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)





    /**
     * This companion object holds various strings as keys for storing and accessing
     * the settings in [SharedPreferences].
     */
    companion object {
        //Settings keys
        private const val KEY_LANGUAGE = "language"
        private const val KEY_TRACKING_ENABLED = "tracking_enabled"
        private const val KEY_TRACKING_REGULARITY = "tracking_regularity"
        private const val KEY_BATTERY_DEPENDENCY_ENABLED = "battery_dependency_enabled"
        private const val KEY_BATTERY_DEPENDENCY = "battery_dependency"
        private const val KEY_HAS_DENIED_PERMISSION_BEFORE = "has_denied_permission_before"
    }



    /**
     * Defines the setting: language of this application.
     */
    var language: Language
        get() {
            val jsonString =
                sharedPreferences.getString(
                    KEY_LANGUAGE,
                    Json.encodeToString(getDefaultLanguage())
                ) ?: ""
            return Json.decodeFromString<Language>(jsonString)
        }
        set(language) {
            sharedPreferences.edit().putString(
                KEY_LANGUAGE,
                Json.encodeToString(language)
            ).apply()
        }

    private fun getDefaultLanguage(): Language {
        return Language.getLanguageFromString("de")
    }



    /**
     * Defines the setting: is [com.pseteamtwo.allways.data.trip.tracking] currently allowed to run.
     */
    var isTrackingEnabled: Boolean
        get() =
            sharedPreferences.getBoolean(
                KEY_TRACKING_ENABLED,
                true)
        set(isTrackingEnabled) {
            sharedPreferences.edit().putBoolean(
                KEY_TRACKING_ENABLED,
                isTrackingEnabled
            ).apply()
            if (context.hasLocationPermission()) {
                Intent(context, LocationService::class.java).apply {
                    action = if (isTrackingEnabled) ACTION_START else ACTION_STOP
                    context.startService(this)
                }
            }
        }




    /**
     * This setting only effects the application while [isTrackingEnabled] is true.
     * Defines the setting: how often should [com.pseteamtwo.allways.trip.tracking] track
     * per minute.
     * If [isBatteryDependencyEnabled] is true, the get() methode updates this setting according
     * to the setting [batteryDependency] if necessary.
     */
    var trackingRegularity: TrackingRegularity
        get() {
            //update trackingRegularity depending on current battery charge of the device
            //and on the setting of batteryDependency
            if(isBatteryDependencyEnabled) {
                val batteryLevel = getBatteryLevel()
                if(batteryLevel <= batteryDependency.first) {
                    this.trackingRegularity = batteryDependency.second
                    Intent(context, LocationService::class.java).apply {
                        action = ACTION_START
                        context.startService(this)
                    }
                }
            }
            //actual get-method
            val jsonString =
                sharedPreferences.getString(
                    KEY_TRACKING_REGULARITY,
                    Json.encodeToString(getDefaultTrackingRegularity())
                ) ?: ""
            return Json.decodeFromString<TrackingRegularity>(jsonString)
        }
        set(trackingRegularity) {
            if(trackingRegularity == TrackingRegularity.NEVER) {
                isTrackingEnabled = false
            }
            sharedPreferences.edit().putString(
                KEY_TRACKING_REGULARITY,
                Json.encodeToString(trackingRegularity)
            ).apply()
        }

    private fun getDefaultTrackingRegularity(): TrackingRegularity {
        return TrackingRegularity.MEDIUM
    }

    //This function could cause high battery usage referring to Android, but did not cause it
    // in user-tests
    private fun getBatteryLevel(): Int {
        val batteryManager = context.getSystemService(BatteryManager::class.java)!!
        val level = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
        if (level < 0) {
            return 0
        }
        return level
    }



    /**
     * This setting only effects the application while [isTrackingEnabled] is true.
     * Defines the setting: should the setting [trackingRegularity] be dependant on the current
     * battery level of the device this application is running on.
     */
    var isBatteryDependencyEnabled: Boolean
        get() =
            sharedPreferences.getBoolean(
                KEY_BATTERY_DEPENDENCY_ENABLED,
                true)
        set(isBatteryDependencyEnabled) =
            sharedPreferences.edit().putBoolean(
                KEY_BATTERY_DEPENDENCY_ENABLED,
                isBatteryDependencyEnabled
            ).apply()



    /**
     * This setting only effects the application while
     * [isTrackingEnabled] and [isBatteryDependencyEnabled] is true.
     * Defines the setting: if the battery level of the device this application is running on drops
     * below the specified integer, the [trackingRegularity] setting should be changed to the
     * specified [TrackingRegularity].
     */
    var batteryDependency: Pair<Int, TrackingRegularity>
        get() {
            val jsonString =
                sharedPreferences.getString(
                    KEY_BATTERY_DEPENDENCY,
                    Json.encodeToString(getDefaultBatteryDependency())
                ) ?: ""
            return Json.decodeFromString<Pair<Int, TrackingRegularity>>(jsonString)
        }
        set(batteryDependency) {
            sharedPreferences.edit().putString(
                KEY_BATTERY_DEPENDENCY,
                Json.encodeToString(batteryDependency)
            ).apply()
        }

    private fun getDefaultBatteryDependency(): Pair<Int, TrackingRegularity> {
        return Pair(25, TrackingRegularity.RARELY)
    }

    var hasDeniedPermissionBefore: Boolean
        get() =
            sharedPreferences.getBoolean(
                KEY_HAS_DENIED_PERMISSION_BEFORE,
                false)
        set(hasDeniedPermissionBefore) =
            sharedPreferences.edit().putBoolean(
                KEY_HAS_DENIED_PERMISSION_BEFORE,
                hasDeniedPermissionBefore
            ).apply()
}