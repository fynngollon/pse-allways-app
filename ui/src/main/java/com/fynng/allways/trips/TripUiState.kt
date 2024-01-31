package com.fynng.allways.trips

import com.pseteamtwo.allways.trip.Mode
import com.pseteamtwo.allways.trip.Purpose

data class TripUiState(

    val id: String,
    val stages: List<StageUiState>?,
    val purpose: Purpose,
    val mode: Mode,
    val isConfirmed: Boolean,
    val duration: Int,
    val distance: Int,
    val startYear: Int,
    val startMonth: Int,
    val startDay: Int,
    val startHour: Int,
    val startMinute: Int,
    val endYear: Int,
    val endMonth: Int,
    val endDay: Int,
    val endHour: Int,
    val endMinute: Int,
    val startLocationName: String,
    val startLocationLongitude: Double,
    val startLocationLatitude: Double,
    val endLocationName: String,
    val endLocationLongitude: Double,
    val endLocationLatitude: Double,
)
