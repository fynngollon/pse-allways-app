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
                            startLocation = trip.startLocation,
                            endLocation = trip.endLocation,
                            startLocationName = "test",
                            endLocationName = "test",
                            duration = trip.duration,
                            distance = trip.distance,
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

    private fun createStageUiStates(tripId: Long) {
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
                            startLocation = stage.startLocation,
                            endLocation = stage.endLocation,
                            startLocationName = "test",
                            endLocationName = "test",
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