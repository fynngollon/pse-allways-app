package com.fynng.allways.trips

import com.pseteamtwo.allways.trip.Mode
import com.pseteamtwo.allways.trip.Purpose
import java.time.LocalDateTime

data class TripUiState(
    val id: String,
    val stageUiStates: List<StageUiState>,
    val purpose: Purpose?,
    val mode: Mode?,
    val isConfirmed: Boolean,
    val startDateTime: LocalDateTime,
    val endDateTime: LocalDateTime,
    val duration: Int,
    val distance: Int,
    /*val startYear: Int,
    val startMonth: Int,
    val startDay: Int,
    val startHour: Int,
    val startMinute: Int,
    val endYear: Int,
    val endMonth: Int,
    val endDay: Int,
    val endHour: Int,
    val endMinute: Int,*/
    val startLocationName: String,
    val startLocationLongitude: Double,
    val startLocationLatitude: Double,
    val endLocationName: String,
    val endLocationLongitude: Double,
    val endLocationLatitude: Double,
    val createStageUiStates: (String) -> Unit
): Comparable<TripUiState> {
    override fun compareTo(other: TripUiState): Int {
        return -startDateTime.compareTo(other.startDateTime)
    }
}

