package com.pseteamtwo.allways.data.trip

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.pseteamtwo.allways.R
import kotlinx.serialization.Serializable

/**
 * Mode
 *
 * This enum class describes different modes. Every [Stage] contains exactly 1 [Mode] object
 * meaning that that [Stage] got traveled in that [Mode].
 */
@Serializable
enum class Mode(val modeType: Int) {
    /**
     * Represents that no Mode has been set for [Stage] yet.
     */
    NONE(R.string.mode_none),

    /**
     * [Stage] got traveled by walking.
     */
    WALK(R.string.mode_walk),

    /**
     * [Stage] was traveled by running.
     */
    RUNNING(R.string.mode_running),

    /**
     * The means of transportation of [Stage] is an E-Bike.
     */
    E_BIKE(R.string.mode_e_bike),

    /**
     * The means of transportation of [Stage] is an E-Bike.
     */
    BICYCLE(R.string.mode_bicycle),

    /**
     * The means of transportation of [Stage] is a motorcycle.
     */
    MOTORCYCLE(R.string.mode_motorcycle),

    /**
     * The means of transportation of [Stage] is a car
     * and the person traveling the [Stage] was driving.
     */
    CAR_DRIVER(R.string.mode_car_driver),

    /**
     * The means of transportation of [Stage] is a car
     * and the person traveling the [Stage] was not driving but a passenger.
     */
    CAR_PASSENGER(R.string.mode_car_passenger),

    /**
     * The means of transportation of [Stage] is a regional bus.
     */
    REGIONAL_BUS(R.string.mode_regional_bus),

    /**
     * The means of transportation of [Stage] is a long-distance bus.
     */
    LONG_DISTANCE_BUS(R.string.mode_long_distance_bus),

    /**
     * The means of transportation of [Stage] is a tram or a metro.
     */
    TRAM_METRO_TRAIN(R.string.mode_tram_metro_train),

    /**
     * The means of transportation of [Stage] is a regional train.
     */
    REGIONAL_TRAIN(R.string.mode_regional_train),

    /**
     * The means of transportation of [Stage] is a long-distance train.
     */
    LONG_DISTANCE_TRAIN(R.string.mode_long_distance_train),

    /**
     * [Stage] got traveled by something this enumeration does not contain.
     */
    OTHER(R.string.mode_other);


    @Composable
    fun getStringForMode(): String {
        return stringResource(id = this.modeType)
    }
}