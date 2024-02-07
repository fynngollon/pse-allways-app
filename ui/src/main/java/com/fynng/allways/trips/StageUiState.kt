package com.fynng.allways.trips

import android.location.Location
import com.pseteamtwo.allways.trip.Mode
import org.threeten.bp.LocalDateTime


data class StageUiState(
    val id: Long,
    val mode: Mode?,
    val startDateTime: LocalDateTime,
    val endDateTime: LocalDateTime,
    val startLocation: Location,
    val endLocation: Location,
    val startLocationName: String,
    val endLocationName: String,
)
