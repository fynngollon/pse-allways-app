package com.pseteamtwo.allways.settings

import android.content.Context
import android.content.SharedPreferences
import android.os.BatteryManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject


//TODO("maybe don't set default values here but anywhere else")
class AppPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE) //TODO("not sure if private is right")

    //TODO("why is this in a companion object?")
    companion object {
        //Settings keys
        private const val KEY_LANGUAGE = "language"
        private const val KEY_TRACKING_ENABLED = "tracking_enabled"
        private const val KEY_TRACKING_REGULARITY = "tracking_regularity"
        private const val KEY_BATTERY_DEPENDENCY_ENABLED = "battery_dependency_enabled"
        private const val KEY_BATTERY_DEPENDENCY = "battery_dependency"
    }


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
        TODO("Not yet implemented")
    }

    /*
    fun setLanguage(language: Language) {
        TODO("Not yet implemented")
    }
    fun getLanguage(): Language {
        TODO("Not yet implemented")
    }
    */


    var isTrackingEnabled: Boolean
        get() =
            sharedPreferences.getBoolean(
                KEY_TRACKING_ENABLED,
                false)
        set(isTrackingEnabled) =
            sharedPreferences.edit().putBoolean(
                KEY_TRACKING_ENABLED,
                isTrackingEnabled
            ).apply()

    /*
    fun setTracking(tracking: Boolean) {
        TODO("Not yet implemented")
    }
    fun isTrackingActive(): Boolean {
        TODO("Not yet implemented")
    }
    */


    var trackingRegularity: TrackingRegularity
        get() {
            //update trackingRegularity depending on current battery charge of the device
            //and on the setting of batteryDependency
            if(isBatteryDependencyEnabled) {
                val batteryLevel = getBatteryLevel() //TODO("could cause high battery usage")
                if(batteryLevel <= batteryDependency.first) {
                    this.trackingRegularity = batteryDependency.second //TODO("don't know if this uses the set()-function call how it should be")
                }
            }
            //actual get-method
            val jsonString =
                sharedPreferences.getString(
                    KEY_TRACKING_REGULARITY,
                    Json.encodeToString(TrackingRegularity.MEDIUM)
                ) ?: ""
            return Json.decodeFromString<TrackingRegularity>(jsonString)
        }
        set(trackingRegularity) {
            sharedPreferences.edit().putString(
                KEY_TRACKING_REGULARITY,
                Json.encodeToString(trackingRegularity)
            ).apply()
        }

    //TODO("Not sure if this code does what it is intended to do but cant test it yet")
    private fun getBatteryLevel(): Double {
        val batteryManager = context.getSystemService(BatteryManager::class.java)!!
        val level = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
        val scale = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER)

        if (level < 0 || scale <= 0) {
            return 0.0
        }

        return (level / scale.toDouble()) * 100.0
    }

    /*
    fun setTrackingRegularity(trackingRegularity: TrackingRegularity) {
        TODO("Not yet implemented")
    }
    fun getTrackingRegularity(): TrackingRegularity {
        TODO("Not yet implemented")
    }
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

    /*
    fun setBatteryDependant(isBatteryDependant: Boolean) {
        TODO("Not yet implemented")
    }
    fun isBatteryDependant(): Boolean {
        TODO("Not yet implemented")
    }
    */


    var batteryDependency: Pair<Int, TrackingRegularity>
        get() {
            val jsonString =
                sharedPreferences.getString(
                    KEY_BATTERY_DEPENDENCY,
                    Json.encodeToString(Pair(25, TrackingRegularity.RARELY))
                ) ?: ""
            return Json.decodeFromString<Pair<Int, TrackingRegularity>>(jsonString)
        }
        set(batteryDependency) {
            sharedPreferences.edit().putString(
                KEY_BATTERY_DEPENDENCY,
                Json.encodeToString(batteryDependency)
            ).apply()
        }

    /*
    fun setBatteryDependency(batteryCharge: Int, trackingRegularity: TrackingRegularity) {
        TODO("Not yet implemented")
    }
    fun getRegularityForBattery(): TrackingRegularity {
        TODO("Not yet implemented")
    }
    */
}