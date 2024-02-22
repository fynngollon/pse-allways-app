package com.pseteamtwo.allways.trips

import com.pseteamtwo.allways.trip.Mode
import com.pseteamtwo.allways.trip.Purpose
import org.osmdroid.util.GeoPoint
import org.threeten.bp.LocalDateTime


data class TripUiState(
    val id: Long,
    val stageUiStates: List<StageUiState>,
    val purpose: Purpose,
    val mode: Mode,
    val isConfirmed: Boolean,
    val startDateTime: LocalDateTime,
    val endDateTime: LocalDateTime,
    val startLocation: GeoPoint,
    val endLocation: GeoPoint,
    val startLocationName: String,
    val endLocationName: String,
    val duration: Long,
    val distance: Int,
    val createStageUiStates: () -> Unit,
    val addStageUiStateBefore: () -> Unit,
    val addStageUiStateAfter: () -> Unit,
    val updateTrip: () -> Unit
): Comparable<TripUiState> {
    override fun compareTo(other: TripUiState): Int {
        return -startDateTime.compareTo(other.startDateTime)
    }
}

