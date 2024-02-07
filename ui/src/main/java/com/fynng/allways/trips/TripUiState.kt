package com.fynng.allways.trips

import android.location.Location
import com.pseteamtwo.allways.trip.Mode
import com.pseteamtwo.allways.trip.Purpose
import org.threeten.bp.LocalDateTime


data class TripUiState(
    val id: Long,
    val stageUiStates: List<StageUiState>,
    val purpose: Purpose?,
    val mode: Mode?,
    val isConfirmed: Boolean,
    val startDateTime: LocalDateTime,
    val endDateTime: LocalDateTime,
    val startLocation: Location,
    val endLocation: Location,
    val startLocationName: String,
    val endLocationName: String,
    val duration: Long,
    val distance: Int,
    val createStageUiStates: (String) -> Unit
): Comparable<TripUiState> {
    override fun compareTo(other: TripUiState): Int {
        return -startDateTime.compareTo(other.startDateTime)
    }
}

