package com.pseteamtwo.allways.data.settings

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.pseteamtwo.allways.R
import kotlinx.serialization.SerialName
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
enum class TrackingRegularity(val regularity: Long, val regularity_id: Int) {
    /**
     * This tracking regularity means that the tracking is disabled.
     */
    @SerialName("never")
    NEVER(0, R.string.tracking_regularity_never),

    /**
     * This regularity means that the tracking should be done once per minute.
     */
    @SerialName("rarely")
    RARELY(60000, R.string.tracking_regularity_rarely),

    /**
     * This regularity means that the tracking should be done twice per minute.
     */
    @SerialName("medium")
    MEDIUM(30000, R.string.tracking_regularity_medium),

    /**
     * This regularity means that the tracking should be done 6 times per minute.
     */
    @SerialName("often")
    OFTEN(10000, R.string.tracking_regularity_often);

    override fun toString(): String {
        return this.name + "(" + this.regularity + ")"
    }

    @Composable
    fun getStringForPurpose(): String {
        return stringResource(id = this.regularity_id)
    }
}