package com.pseteamtwo.allways.settings

import kotlinx.serialization.Serializable

/**
 * Tracking regularity
 *
 * This enum class describes the regularity of the [com.pseteamtwo.allways.trip.tracking], meaning
 * how often the tracking captures the device's location per minute.
 * The tracking regularity is set in the setting [AppPreferences] and can be changed by the user.
 *
 * @property regularity Describes for every [TrackingRegularity] object how much time should pass
 * between 2 trackings of the device's location.
 */
@Serializable
enum class TrackingRegularity(val regularity: Int) {
    /**
     * This tracking regularity means that the tracking is disabled.
     */
    NEVER(0),

    /**
     * This regularity means that the tracking should be done once per minute.
     */
    RARELY(60),

    /**
     * This regularity means that the tracking should be done twice per minute.
     */
    MEDIUM(30),

    /**
     * This regularity means that the tracking should be done 6 times per minute.
     */
    OFTEN(10);
}