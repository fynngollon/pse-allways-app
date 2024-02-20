package com.pseteamtwo.allways.trip

/**
 * Mode
 *
 * This enum class describes different modes. Every [Stage] contains exactly 1 [Mode] object
 * meaning that that [Stage] got traveled in that [Mode].
 */
enum class Mode {
    /**
     * Represents that no Mode has been set for [Stage] yet.
     */
    NONE,

    /**
     * [Stage] got traveled by walking.
     */
    WALK,

    /**
     * The means of transportation of [Stage] is an E-Bike.
     */
    E_BIKE,

    /**
     * The means of transportation of [Stage] is an E-Bike.
     */
    BICYCLE,

    /**
     * The means of transportation of [Stage] is a motorcycle.
     */
    MOTORCYCLE,

    /**
     * The means of transportation of [Stage] is a car
     * and the person traveling the [Stage] was driving.
     */
    CAR_DRIVER,

    /**
     * The means of transportation of [Stage] is a car
     * and the person traveling the [Stage] was not driving but a passenger.
     */
    CAR_PASSENGER,

    /**
     * The means of transportation of [Stage] is a regional bus.
     */
    REGIONAL_BUS,

    /**
     * The means of transportation of [Stage] is a long-distance bus.
     */
    LONG_DISTANCE_BUS,

    /**
     * The means of transportation of [Stage] is a tram or a metro.
     */
    TRAM_METRO_TRAIN,

    /**
     * The means of transportation of [Stage] is a regional train.
     */
    REGIONAL_TRAIN,

    /**
     * The means of transportation of [Stage] is a long-distance train.
     */
    LONG_DISTANCE_TRAIN,

    /**
     * [Stage] got traveled by something this enumeration does not contain.
     */
    OTHER
}