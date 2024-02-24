package com.pseteamtwo.allways.trips

import com.pseteamtwo.allways.trip.Mode
import org.osmdroid.util.GeoPoint
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime

data class StageUiState(
    val id: Int,
    val stageId: Long,
    val mode: Mode,
    val isInDatabase: Boolean,
    val isToBeAddedBefore: Boolean,
    val isFirstStageOfTrip: Boolean,
    val isLastStageOfTrip: Boolean,
    val startDateTime: LocalDateTime,
    val endDateTime: LocalDateTime,
    val startLocation: GeoPoint,
    val endLocation: GeoPoint,
    val startLocationName: String,
    val endLocationName: String,
    val getPreviousStageUiState: () -> StageUiState?,
    val getNextStageUiState: () -> StageUiState?,
    val setMode: (Mode) -> Unit,
    val setStartDate: (LocalDate) -> Unit,
    val setEndDate: (LocalDate) -> Unit,
    val setStartTime: (Int, Int) -> Unit,
    val setEndTime: (Int, Int) -> Unit,
    val setStartLocation: (GeoPoint) -> Unit,
    val setEndLocation: (GeoPoint) -> Unit,
    val setStartLocationName: (String) -> Unit,
    val setEndLocationName: (String) -> Unit,
): Comparable<StageUiState> {

    val startDate: LocalDate
        get() = startDateTime.toLocalDate()

    val endDate: LocalDate
        get() = endDateTime.toLocalDate()

    val startHour: Int
        get() = startDateTime.hour

    val startMinute: Int
        get() = startDateTime.minute

    val endHour: Int
        get() = endDateTime.hour

    val endMinute: Int
        get() = endDateTime.minute

    override fun compareTo(other: StageUiState): Int {
        return -startDateTime.compareTo(other.startDateTime)
    }
}
