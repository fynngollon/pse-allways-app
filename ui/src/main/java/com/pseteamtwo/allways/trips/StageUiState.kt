package com.pseteamtwo.allways.trips

import com.pseteamtwo.allways.trip.Mode
import org.osmdroid.util.GeoPoint
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime

/**
 * The UI state of a [stage][com.pseteamtwo.allways.trip.Stage].
 *
 * This UI state contains all the necessary information to display the respective stage in the UI.
 *
 * @property id the unique identification number of this stage UI state inside the UI layer.
 * @property stageId The unique identification number of the respective stage in the Data layer (is
 * 0 if no respective stage exists yet).
 * @property mode the mode of the respective stage.
 * @property isInDataBase whether a stage to this stage UI state already exists in the app's trip database
 * (or a new stage will have to be created).
 * @property isToBeAddedBefore whether the new stage is to be added before the first stage of a trip
 * (or after the last stage of the trip).
 * @property isFirstStageOfTrip whether the respective state is the first stage of the trip it
 * belongs to.
 * @property isLastStageOfTrip whether the respective state is the last stage of the trip it belongs
 * to.
 * @property startDateTime the date and time of the respective stage's start.
 * @property endDateTime the date and time of the respective stage's end.
 * @property startLocation the location of the respective stage's start.
 * @property endLocation the location of the respective stage's end.
 * @property startLocationName the name of the respective stage's start location.
 * @property endLocationName the name of the respective stage's end location.
 * @property getPreviousStageUiState the function for getting the stage UI state before this one.
 * @property getNextStageUiState the function for getting the stage UI state after this one.
 * @property setMode the function for updating this stage Ui state's mode in the [TripsViewModel]it
 * belongs to.
 * @property setStartDate the function for updating this stage Ui state's start date in the
 * [TripsViewModel] it belongs to.
 * @property setEndDate the function for updating this stage Ui state's end date in the
 * [TripsViewModel] it belongs to.
 * @property setStartTime the function for updating this stage Ui state's start time in the
 * [TripsViewModel] it belongs to.
 * @property setEndTime the function for updating this stage Ui state's end time in the
 * [TripsViewModel] it belongs to.
 * @property setStartLocation the function for updating this stage Ui state's start location in the
 * [TripsViewModel] it belongs to.
 * @property setEndLocation the function for updating this stage Ui state's end location in the
 * [TripsViewModel] it belongs to.
 * @property setStartLocation the function for updating this stage Ui state's start location in the
 * [TripsViewModel] it belongs to.
 * @property setEndLocation the function for updating this stage Ui state's end location in the
 * [TripsViewModel] it belongs to.
 * @constructor Creates a stage with the specified properties.
 * The properties [startDate], [endDate], [startHour], [startMinute], [endHour]
 *  * and [endMinute] are computed through the provided list of [GpsPoint]s.
 * */
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
