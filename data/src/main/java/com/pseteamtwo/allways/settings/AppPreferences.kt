package com.pseteamtwo.allways.settings

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken




//TODO("maybe don't set default values here but anywhere else")
class AppPreferences(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE) //TODO("not sure if private is right")

    private val gson = Gson()

    //TODO("why is this in a companion object?")
    companion object {
        //Settings keys
        private val KEY_LANGUAGE = "language"
        private val KEY_TRACKING_ENABLED = "tracking_enabled"
        private val KEY_TRACKING_REGULARITY = "tracking_regularity"
        private val KEY_BATTERY_DEPENDENCY_ENABLED = "battery_dependency_enabled"
        private val KEY_BATTERY_DEPENDENCY = "battery_dependency"
    }


    var language: Language
        get() {
            val jsonString =
                sharedPreferences.getString(
                    KEY_LANGUAGE,
                    gson.toJson(getDefaultLanguage())
                )
            return gson.fromJson(jsonString, Language::class.java)
        }
        set(language) {
            sharedPreferences.edit().putString(
                KEY_LANGUAGE,
                gson.toJson(language)
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
                val batteryLevel = getBatteryLevel() //TODO("getBatteryLevel function implementation")
                if(batteryLevel <= batteryDependency.first) {
                    this.trackingRegularity = batteryDependency.second //TODO("don't know if this uses the set()-function call how it should be")
                }
            }
            //actual get-method
            val jsonString =
                sharedPreferences.getString(
                    KEY_TRACKING_REGULARITY,
                    gson.toJson(TrackingRegularity.MEDIUM)
                )
            return gson.fromJson(jsonString, TrackingRegularity::class.java)
        }
        set(trackingRegularity) {
            sharedPreferences.edit().putString(
                KEY_TRACKING_REGULARITY,
                gson.toJson(trackingRegularity)
            ).apply()
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
            val typeOfSrc = object : TypeToken<Pair<Int, TrackingRegularity>>() {}.type
            val jsonString =
                sharedPreferences.getString(
                    KEY_BATTERY_DEPENDENCY,
                    gson.toJson(Pair(25, TrackingRegularity.RARELY), typeOfSrc)
                )
            return gson.fromJson(jsonString, typeOfSrc)
        }
        set(batteryDependency) {
            val typeOfSrc = object : TypeToken<Pair<Int, TrackingRegularity>>() {}.type
            sharedPreferences.edit().putString(
                KEY_BATTERY_DEPENDENCY,
                gson.toJson(batteryDependency, typeOfSrc)
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