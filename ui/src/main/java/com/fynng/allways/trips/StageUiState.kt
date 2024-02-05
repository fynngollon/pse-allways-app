package com.fynng.allways.trips

import com.pseteamtwo.allways.trip.Mode
import java.time.LocalDateTime

data class StageUiState(
    val id: String,
    val mode: Mode?,
    val startDateTime: LocalDateTime,
    val endDateTime: LocalDateTime,
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
)
