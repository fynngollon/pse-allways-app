package com.pseteamtwo.allways.trips

import com.pseteamtwo.allways.trip.Purpose
import org.osmdroid.util.GeoPoint
import org.threeten.bp.LocalDateTime


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
    val updateTrip: () -> Unit,
    var sendToServer: Boolean
): Comparable<TripUiState> {
    override fun compareTo(other: TripUiState): Int {
        return -startDateTime.compareTo(other.startDateTime)
    }
}

