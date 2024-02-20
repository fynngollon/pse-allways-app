package com.fynng.allways.trips

import com.pseteamtwo.allways.trip.Mode
import org.osmdroid.util.GeoPoint
import org.threeten.bp.LocalDateTime

data class StageUiState(
    val id: Long,
    val mode: Mode,
    val startDateTime: LocalDateTime,
    val endDateTime: LocalDateTime,
    val startLocation: GeoPoint,
    val endLocation: GeoPoint,
    val startLocationName: String,
    val endLocationName: String,
    val setMode: (Mode) -> Unit,
    val setStartTime: (Int, Int) -> Unit,
    val setEndTime: (Int, Int) -> Unit,
    val setStartLocation: (GeoPoint) -> Unit,
    val setEndLocation: (GeoPoint) -> Unit,
    val setStartLocationName: (String) -> Unit,
    val setEndLocationName: (String) -> Unit,
    val updateStage: () -> Unit
): Comparable<StageUiState> {
    override fun compareTo(other: StageUiState): Int {
        return -startDateTime.compareTo(other.startDateTime)
    }
}
