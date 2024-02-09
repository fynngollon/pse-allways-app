package com.fynng.allways.trips


import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pseteamtwo.allways.trip.GpsPoint
import com.pseteamtwo.allways.trip.Mode

import com.pseteamtwo.allways.trip.repository.TripAndStageRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDateTime


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
                val tripUiStates = _tripsUiState.value.tripUiStates
                val tripIndex = tripUiStates.indexOfFirst{it.id == tripId}
                val tripUiState = tripUiStates[tripIndex]
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
                            startLocationName = "test", //TODO
                            endLocationName = "test", //TODO
                        )
                    )
                }
                _tripsUiState.update {
                    it ->
                    it.copy(
                        tripUiStates =
                            tripUiStates.slice(0 until tripIndex)
                            + tripUiState.copy(
                                stageUiStates = stageUiStates
                            )
                            + tripUiStates.slice(tripIndex + 1 until tripUiStates.size)
                    )
                }
            }
        }
    }

    fun editStageUiState(
        tripId: Long,
        stageId: Long,
        mode: Mode? = _tripsUiState.value.tripUiStates.find{it.id == tripId}!!.stageUiStates.find{it.id == stageId }?.mode,
        startDateTime: LocalDateTime = _tripsUiState.value.tripUiStates.find{it.id == tripId}!!.stageUiStates.find{it.id == stageId }!!.startDateTime,
        endDateTime: LocalDateTime = _tripsUiState.value.tripUiStates.find{it.id == tripId}!!.stageUiStates.find{it.id == stageId }!!.endDateTime,
        startLocation: Location = _tripsUiState.value.tripUiStates.find{it.id == tripId}!!.stageUiStates.find{it.id == stageId }!!.startLocation,
        endLocation: Location = _tripsUiState.value.tripUiStates.find{it.id == tripId}!!.stageUiStates.find{it.id == stageId }!!.endLocation,
        startLocationName: String = _tripsUiState.value.tripUiStates.find{it.id == tripId}!!.stageUiStates.find{it.id == stageId }!!.startLocationName,
        endLocationName: String = _tripsUiState.value.tripUiStates.find{it.id == tripId}!!.stageUiStates.find{it.id == stageId }!!.endLocationName,
    ) {
        val tripUiStates = _tripsUiState.value.tripUiStates
        val tripIndex = tripUiStates.indexOfFirst{it.id == tripId}
        val tripUiState = tripUiStates[tripIndex]
        val stageUiStates = tripUiState.stageUiStates
        val stageIndex = stageUiStates.indexOfFirst{it.id == stageId}
        val stageUiState = stageUiStates[stageIndex]

        _tripsUiState.update {
            it.copy(
                tripUiStates =
                    tripUiStates.slice(0 until tripIndex)
                    + tripUiState.copy(
                        stageUiStates =
                            stageUiStates.slice(0 until stageIndex)
                            + stageUiState.copy(
                                mode = mode,
                                startDateTime = startDateTime,
                                endDateTime = endDateTime,
                                startLocation = startLocation,
                                endLocation = endLocation,
                                startLocationName = startLocationName,
                                endLocationName = endLocationName
                            )
                            + stageUiStates.slice(stageIndex + 1 until stageUiStates.size)
                    )
                    + tripUiStates.slice(tripIndex + 1 until tripUiStates.size)
            )
        }
    }



    fun updateStage(
        stageId: Long,
        mode: Mode?,
        startDateTime: LocalDateTime,
        endDateTime: LocalDateTime,
        startLocation: GpsPoint,
        endLocation: GpsPoint,
        startLocationName: String,
        endLocationName: String,
    ) {

    }

    fun updateStages(tripId: Long) {

    }

    fun updateTrip() {

    }
}