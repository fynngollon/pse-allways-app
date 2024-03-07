package com.pseteamtwo.allways.trip

import kotlinx.serialization.Serializable

/**
 * Mode
 *
 * This enum class describes different modes. Every [Stage] contains exactly 1 [Mode] object
 * meaning that that [Stage] got traveled in that [Mode].
 */
@Serializable
enum class Mode(val modeType: String) {
    /**
     * Represents that no Mode has been set for [Stage] yet.
     */
    NONE("Nicht eingetragen"),

    /**
     * [Stage] got traveled by walking.
     */
    WALK("Laufen"),

    /**
     * [Stage] was traveled by running.
     */
    RUNNING("Rennen"),

    /**
     * The means of transportation of [Stage] is an E-Bike.
     */
    E_BIKE("E-Bike"),

    /**
     * The means of transportation of [Stage] is an E-Bike.
     */
    BICYCLE("Fahrrade"),

    /**
     * The means of transportation of [Stage] is a motorcycle.
     */
    MOTORCYCLE("Motorrad"),

    /**
     * The means of transportation of [Stage] is a car
     * and the person traveling the [Stage] was driving.
     */
    CAR_DRIVER("Fahrer im Auto"),

    /**
     * The means of transportation of [Stage] is a car
     * and the person traveling the [Stage] was not driving but a passenger.
     */
    CAR_PASSENGER("Beifahrer"),

    /**
     * The means of transportation of [Stage] is a regional bus.
     */
    REGIONAL_BUS("Regionaler Bus"),

    /**
     * The means of transportation of [Stage] is a long-distance bus.
     */
    LONG_DISTANCE_BUS("Fernbus"),

    /**
     * The means of transportation of [Stage] is a tram or a metro.
     */
    TRAM_METRO_TRAIN("U-Bahn"),

    /**
     * The means of transportation of [Stage] is a regional train.
     */
    REGIONAL_TRAIN("Regionaler Zug"),

    /**
     * The means of transportation of [Stage] is a long-distance train.
     */
    LONG_DISTANCE_TRAIN("Langstreckenzug"),

    /**
     * [Stage] got traveled by something this enumeration does not contain.
     */
    OTHER("Andere")
}