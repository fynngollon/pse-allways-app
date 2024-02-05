package com.fynng.allways.trips


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.pseteamtwo.allways.trip.repository.TripAndStageRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class TripsViewModel(private val tripAndStageRepository: TripAndStageRepository) : ViewModel() {
    private var _tripsUiState: MutableStateFlow<TripsUiState> = MutableStateFlow(TripsUiState(loading = true))
    val tripsUiState: StateFlow<TripsUiState> = _tripsUiState.asStateFlow()

    init {
        viewModelScope.launch {
            tripAndStageRepository.observeAllTrips().collect {
                trips ->
                val tripUiStates: List<TripUiState> = emptyList()
                for (trip in trips) {
                    tripUiStates.plus(
                        TripUiState(
                            id = trip.id,
                            stageUiStates = emptyList(),
                            purpose =  trip.purpose,
                            mode = null,
                            isConfirmed = trip.isConfirmed,
                            startDateTime = trip.startDateTime,
                            endDateTime = trip.endDateTime,
                            duration = trip.duration,
                            distance = trip.distance,
                            /*startYear = trip.startDateTime.year,
                            startMonth = trip.startDateTime.monthValue,
                            startDay = trip.startDateTime.dayOfMonth,
                            startHour = trip.startDateTime.hour,
                            startMinute =  trip.startDateTime.minute,
                            endYear = trip.endDateTime.year,
                            endMonth = trip.endDateTime.monthValue,
                            endDay = trip.endDateTime.dayOfMonth,
                            endHour = trip.endDateTime.hour,
                            endMinute =  trip.endDateTime.minute,*/
                            startLocationName = "test",
                            startLocationLatitude = trip.startLocation.latitude,
                            startLocationLongitude = trip.startLocation.longitude,
                            endLocationName = "test",
                            endLocationLatitude = trip.endLocation.latitude,
                            endLocationLongitude = trip.endLocation.longitude,
                            createStageUiStates = { createStageUiStates(trip.id) }
                        )
                    )
                }
                _tripsUiState.update {
                    it.copy(
                        tripUiStates = tripUiStates,
                        loading = false,
                        serverConnectionFailed = false
                    )
                }
            }
        }
    }

    private fun createStageUiStates(tripId: String) {
        viewModelScope.launch {
            tripAndStageRepository.observeStagesOfTrip(tripId).collect {
                stages ->
                val stageUiStates: List<StageUiState> = emptyList()
                for (stage in stages) {
                    stageUiStates.plus(
                        StageUiState(
                            id = stage.id,
                            mode = stage.mode,
                            startDateTime = stage.startDateTime,
                            endDateTime = stage.endDateTime,
                            /*startYear = stage.startDateTime.year,
                            startMonth = stage.startDateTime.monthValue,
                            startDay = stage.startDateTime.dayOfMonth,
                            startHour = stage.startDateTime.hour,
                            startMinute = stage.startDateTime.minute,
                            endYear = stage.endDateTime.year,
                            endMonth = stage.endDateTime.monthValue,
                            endDay = stage.endDateTime.dayOfMonth,
                            endHour = stage.endDateTime.hour,
                            endMinute = stage.endDateTime.minute,*/
                            startLocationName = "test",
                            startLocationLongitude = stage.startLocation.longitude,
                            startLocationLatitude = stage.startLocation.latitude,
                            endLocationName = "test",
                            endLocationLongitude = stage.endLocation.longitude,
                            endLocationLatitude = stage.endLocation.latitude
                        )
                    )
                }
                _tripsUiState.update {
                    it ->
                    it.copy(
                        tripUiStates = it.tripUiStates.plus(it.tripUiStates.find{it.id == tripId}!!.copy(stageUiStates = stageUiStates))
                    )
                }
            }
        }
    }
}