package com.pseteamtwo.allways.trips

import com.pseteamtwo.allways.trip.Purpose
import org.osmdroid.util.GeoPoint
import org.threeten.bp.LocalDateTime

/**
 * The UI state of a [trip][com.pseteamtwo.allways.trip.Trip].
 *
 * This UI state contains all the necessary information to display the respective stage in the UI.
 *
 * @property id the unique identification number of this trip UI state inside the UI layer.
 * @property stageId the unique identification number of the respective trip in the Data layer
 * (is 0 if no respective trip exists yet).
 * @property purpose the purpose of the respective trip.
 * @property isConfirmed whether this trip has been confirmed by the user.
 * @property stageUiStates the stage UI states belonging to this trip UI state.
 * @property startDateTime the date and time of the respective trip's start.
 * @property endDateTime the date and time of the respective trip's end.
 * @property startLocation the location of the respective trip's start.
 * @property endLocation the location of the respective trip's end.
 * @property startLocationName the name of the respective trip's start location.
 * @property endLocationName the name of the respective trip's end location.
 * @property duration the duration of the respective trip.
 * @property distance the distance of the respective trip.
 * @property deleteTrip the function for deleting this trip UI state and the respective trip.
 * @property createStageUiStates the function for creating the stage UI states belonging to this
 * trip UI state
 * @property addStageUiStateBefore the function for adding a new stage UI state before this trip UI
 * state's first stage UI state.
 * @property addStageUiStateAfter the function for adding a new stage UI state after this trip UI
 * state's last stage UI state.
 * @property setPurpose the function for setting this trip UI state's purpose.
 * @property updateTrip the function for updating the respective trip in the database or creating
 * one.
 * @property sendToServer the function for sending the respective trip to the server.
 * */
data class TripUiState(
    val id: Long,
    val tripId: Long,
    val purpose: Purpose,
    val isConfirmed: Boolean,
    val stageUiStates: List<StageUiState>,
    val startDateTime: LocalDateTime,
    val endDateTime: LocalDateTime,
    val startLocation: GeoPoint,
    val endLocation: GeoPoint,
    val startLocationName: String,
    val endLocationName: String,
    val duration: Long,
    val distance: Int,
    val deleteTrip: () -> Unit,
    val createStageUiStates: () -> Unit,
    val addStageUiStateBefore: () -> Unit,
    val addStageUiStateAfter: () -> Unit,
    val setPurpose: (Purpose) -> Unit,
    val updateTrip: () -> Unit,
    var sendToServer: Boolean
): Comparable<TripUiState> {
    override fun compareTo(other: TripUiState): Int {
        return -startDateTime.compareTo(other.startDateTime)
    }
}

