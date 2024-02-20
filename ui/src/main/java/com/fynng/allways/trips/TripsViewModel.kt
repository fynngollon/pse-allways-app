package com.fynng.allways.trips



import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.fynng.allways.map.addressToString

import com.pseteamtwo.allways.trip.Mode

import com.pseteamtwo.allways.trip.repository.TripAndStageRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.osmdroid.bonuspack.location.GeocoderNominatim
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.threeten.bp.LocalDateTime
import org.threeten.bp.LocalTime
import org.threeten.bp.ZoneOffset
import java.util.Locale



class TripsViewModel(private val tripAndStageRepository: TripAndStageRepository) : ViewModel() {
    private var _tripsUiState: MutableStateFlow<TripsUiState> = MutableStateFlow(TripsUiState(loading = true))
    val tripsUiState: StateFlow<TripsUiState> = _tripsUiState.asStateFlow()

    private val geocoder: GeocoderNominatim = GeocoderNominatim(Locale.getDefault(), Configuration.getInstance().userAgentValue)

    init {
        viewModelScope.launch {
            tripAndStageRepository.observeAllTrips().collect {
                trips ->
                val tripUiStates: MutableList<TripUiState> = mutableListOf()
                for (trip in trips) {

                    val tripUiState = TripUiState(
                        id = trip.id,
                        stageUiStates = emptyList(),
                        purpose =  trip.purpose,
                        mode = Mode.NONE, //TODO: Methode in Trip, die aus Stages das meistgenutze Verkerhsmittel für einen Trip bestimmt?
                        isConfirmed = trip.isConfirmed,
                        startDateTime = trip.startDateTime,
                        endDateTime = trip.endDateTime,
                        startLocation = GeoPoint(trip.startLocation),
                        endLocation = GeoPoint(trip.endLocation),
                        startLocationName = "Lädt...",
                        endLocationName = "Lädt...",
                        duration = trip.duration,
                        distance = trip.distance,
                        createStageUiStates = {createStageUiStates(trip.id)},
                        addStageUiStateAfter = {addStageUiStateAfter(trip.id)},
                        updateTrip = {updateTrip(trip.id)}
                    )

                    tripUiStates.add(tripUiState)

                    CoroutineScope(Dispatchers.IO).launch{
                        var addresses = withContext(Dispatchers.IO) {geocoder.getFromLocation(trip.startLocation.latitude, trip.startLocation.longitude, 1)}
                        val startLocationName = addressToString(addresses[0])
                        addresses = withContext(Dispatchers.IO) {geocoder.getFromLocation(trip.startLocation.latitude, trip.startLocation.longitude, 1)}
                        val endLocationName = addressToString(addresses[0])
                        updateTripUiState(
                            trip.id,
                            tripUiState.copy(
                                startLocationName = startLocationName,
                                endLocationName = endLocationName
                            )
                        )
                    }
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
                val tripUiStates = getTripUiStates()
                val tripUiStateIndex = getTripUiStateIndex(tripId)
                val tripUiState = tripUiStates[tripUiStateIndex]
                val stageUiStates: MutableList<StageUiState> = mutableListOf()
                for (stage in stages) {
                    val stageUiState = StageUiState(
                        id = stage.id,
                        mode = stage.mode,
                        startDateTime = stage.startDateTime,
                        endDateTime = stage.endDateTime,
                        startLocation = GeoPoint(stage.startLocation),
                        endLocation = GeoPoint(stage.endLocation),
                        startLocationName = "Lädt...",
                        endLocationName = "Lädt...",
                        setMode = {
                            mode: Mode ->
                            setStageUiStageMode(
                                tripId = tripId,
                                stageId = stage.id,
                                mode = mode
                            )
                        },
                        setStartTime = {
                            hour: Int, minute: Int ->
                            setStageUiStateStartTime(
                                tripId = tripId,
                                stageId = stage.id,
                                hour = hour,
                                minute = minute
                            )
                        },
                        setEndTime = {
                            hour: Int, minute: Int ->
                            setStageUiStateEndTime(
                                tripId = tripId,
                                stageId = stage.id,
                                hour = hour,
                                minute = minute)
                        },
                        setStartLocation = {
                            geoPoint: GeoPoint ->
                            setStageUiStateStartLocation(
                                tripId = tripId,
                                stageId = stage.id,
                                geoPoint = geoPoint
                            )
                        },
                        setEndLocation = {
                            geoPoint: GeoPoint ->
                            setStageUiStateEndLocation(
                                tripId = tripId,
                                stageId = stage.id,
                                geoPoint = geoPoint
                            )
                        },
                        setStartLocationName = {
                            startLocationName: String ->
                            setStageUiStateStartLocationName(
                                tripId = tripId,
                                stageId = stage.id,
                                startLocationName = startLocationName
                            )
                        },
                        setEndLocationName = {
                            startLocationName: String ->
                            setStageUiStateEndLocationName(
                                tripId = tripId,
                                stageId = stage.id,
                                startLocationName = startLocationName
                            )
                        },
                        updateStage = {
                            updateStage(tripId, stage.id)
                        }
                    )

                    stageUiStates.add(stageUiState)
                }

                updateTripUiState(
                    tripId = tripId,
                    tripUiState.copy(stageUiStates = stageUiStates)
                )
                for (stageUiState in stageUiStates) {
                    CoroutineScope(Dispatchers.IO).launch{
                        var addresses = withContext(Dispatchers.IO) {geocoder.getFromLocation(stageUiState.startLocation.latitude, stageUiState.startLocation.longitude, 1)}
                        val startLocationName = addressToString(addresses[0])
                        addresses = withContext(Dispatchers.IO) {geocoder.getFromLocation(stageUiState.startLocation.latitude, stageUiState.startLocation.longitude, 1)}
                        val endLocationName = addressToString(addresses[0])
                        updateStageUiState(
                            tripId = tripId,
                            stageId = stageUiState.id,
                            stageUiState.copy(
                                startLocationName = startLocationName,
                                endLocationName = endLocationName
                            )
                        )
                    }
                }
            }
        }
    }

    private fun getTripUiStates(): List<TripUiState> {
        return _tripsUiState.value.tripUiStates
    }
    private fun getTripUiStateIndex(tripId: Long): Int {
        return getTripUiStates().indexOfFirst{it.id == tripId}
    }

    private fun getTripUiState(tripId: Long): TripUiState {
        return getTripUiStates()[getTripUiStateIndex(tripId)]
    }

    private fun getStageUiStates(tripId: Long): List<StageUiState> {
        return getTripUiState(tripId).stageUiStates
    }

    private fun getStageUiStateIndex(tripId: Long, stageId: Long): Int {
        return getStageUiStates(tripId).indexOfFirst{it.id == stageId}
    }

    private fun getStageUiState(tripId: Long, stageId: Long): StageUiState {
        return getStageUiStates(tripId)[getStageUiStateIndex(tripId, stageId)]
    }

    private fun updateTripUiState(tripId: Long, newTripUiState: TripUiState) {
        val tripUiStates = getTripUiStates()
        val tripUiStateIndex = getTripUiStateIndex(tripId)
        _tripsUiState.update {
            it.copy(
                tripUiStates =
                    tripUiStates.slice(0 until tripUiStateIndex)
                    + newTripUiState
                    + tripUiStates.slice(tripUiStateIndex + 1 until tripUiStates.size)
            )
        }
    }

    private fun updateStageUiState(tripId: Long, stageId: Long, newStageUiState: StageUiState) {
        val tripUiState = getTripUiState(tripId)
        val stageUiStates = getStageUiStates(tripId)
        val stageUiStateIndex = getStageUiStateIndex(tripId, stageId)

        updateTripUiState(
            tripId,
            tripUiState.copy(
                stageUiStates =
                    stageUiStates.slice(0 until stageUiStateIndex)
                    + newStageUiState
                    + stageUiStates.slice(stageUiStateIndex + 1 until stageUiStates.size)
            )
        )
    }

    private fun setStageUiStageMode(
        tripId: Long,
        stageId: Long,
        mode: Mode
    ) {
        updateStageUiState(
            tripId = tripId,
            stageId = stageId,
            newStageUiState = getStageUiState(tripId, stageId).copy(mode = mode)
        )
    }

    private fun setStageUiStateStartTime(
        tripId: Long,
        stageId: Long,
        hour: Int = getStageUiState(tripId, stageId).startDateTime.hour,
        minute: Int = getStageUiState(tripId, stageId).startDateTime.minute
    ) {
        val stageUiState = getStageUiState(tripId, stageId)
        val startDateTime = stageUiState.startDateTime
        updateStageUiState(
            tripId = tripId,
            stageId = stageId,
            newStageUiState = stageUiState.copy(
                startDateTime = LocalDateTime.of(startDateTime.toLocalDate(), LocalTime.of(hour, minute))
            )
        )
    }

    private fun setStageUiStateEndTime(
        tripId: Long,
        stageId: Long,
        hour: Int = getStageUiState(tripId, stageId).endDateTime.hour,
        minute: Int = getStageUiState(tripId, stageId).endDateTime.minute
    ) {
        val stageUiState = getStageUiState(tripId, stageId)
        val endDateTime = stageUiState.endDateTime
        updateStageUiState(
            tripId = tripId,
            stageId = stageId,
            newStageUiState = stageUiState.copy(
                endDateTime = LocalDateTime.of(endDateTime.toLocalDate(), LocalTime.of(hour, minute))
            )
        )
    }

    private fun setStageUiStateStartLocation(
        tripId: Long,
        stageId: Long,
        geoPoint: GeoPoint
    ) {
        val stageUiState = getStageUiState(tripId, stageId)
        updateStageUiState(
            tripId = tripId,
            stageId = stageId,
            newStageUiState = stageUiState.copy(
                startLocation = geoPoint
            )
        )
    }

    private fun setStageUiStateEndLocation(
        tripId: Long,
        stageId: Long,
        geoPoint: GeoPoint
    ) {
        val stageUiState = getStageUiState(tripId, stageId)
        updateStageUiState(
            tripId = tripId,
            stageId = stageId,
            newStageUiState = stageUiState.copy(
                endLocation = geoPoint
            )
        )
    }

    private fun setStageUiStateStartLocationName(
        tripId: Long,
        stageId: Long,
        startLocationName: String
    ) {
        val stageUiState = getStageUiState(tripId, stageId)
        updateStageUiState(
            tripId = tripId,
            stageId = stageId,
            newStageUiState = stageUiState.copy(
                startLocationName = startLocationName
            )
        )
    }

    private fun setStageUiStateEndLocationName(
        tripId: Long,
        stageId: Long,
        startLocationName: String
    ) {
        val stageUiState = getStageUiState(tripId, stageId)
        updateStageUiState(
            tripId = tripId,
            stageId = stageId,
            newStageUiState = stageUiState.copy(
                startLocationName = startLocationName
            )
        )
    }

    private fun updateStage(
        tripId: Long,
        stageId: Long
    ) {
        val stageUiState = getStageUiState(tripId, stageId)
        CoroutineScope(Dispatchers.Default).launch {
            tripAndStageRepository.updateStage(
                stageId = stageUiState.id,
                mode = stageUiState.mode,
                startTime = stageUiState.startDateTime,
                endTime = stageUiState.endDateTime,
                startLocation = stageUiState.startLocation,
                endLocation = stageUiState.endLocation
            )
        }
    }

    private fun updateStages(tripUiState: TripUiState) {
        for (stageUiState in tripUiState.stageUiStates) {
            updateStage(tripUiState.id, stageUiState.id)
        }
    }

    private fun addStageUiStateAfter(tripId: Long) {
        val tripUiState = getTripUiState(tripId)
        val stageUiStates = tripUiState.stageUiStates
        val lastStageUiState = stageUiStates.last()
        val id = lastStageUiState.id + 1
        val location = lastStageUiState.endLocation
        updateTripUiState(
            tripId = tripId,
            newTripUiState = tripUiState.copy(
                stageUiStates = stageUiStates.plus(
                    StageUiState(
                        id = stageUiStates.last().id + 1,
                        mode = Mode.NONE,
                        startDateTime = LocalDateTime.now(),
                        endDateTime = LocalDateTime.now(),
                        startLocation = location,
                        endLocation = location,
                        startLocationName = "-",
                        endLocationName = "-",
                        setMode = {
                                mode: Mode ->
                            setStageUiStageMode(
                                tripId = tripId,
                                stageId = id,
                                mode = mode
                            )
                        },
                        setStartTime = {
                                hour: Int, minute: Int ->
                            setStageUiStateStartTime(
                                tripId = tripId,
                                stageId = id,
                                hour = hour,
                                minute = minute
                            )
                        },
                        setEndTime = {
                                hour: Int, minute: Int ->
                            setStageUiStateEndTime(
                                tripId = tripId,
                                stageId = id,
                                hour = hour,
                                minute = minute)
                        },
                        setStartLocation = {
                                geoPoint: GeoPoint ->
                            setStageUiStateStartLocation(
                                tripId = tripId,
                                stageId = id,
                                geoPoint = geoPoint
                            )
                        },
                        setEndLocation = {
                                geoPoint: GeoPoint ->
                            setStageUiStateEndLocation(
                                tripId = tripId,
                                stageId = id,
                                geoPoint = geoPoint
                            )
                        },
                        setStartLocationName = {
                                startLocationName: String ->
                            setStageUiStateStartLocationName(
                                tripId = tripId,
                                stageId = id,
                                startLocationName = startLocationName
                            )
                        },
                        setEndLocationName = {
                                startLocationName: String ->
                            setStageUiStateEndLocationName(
                                tripId = tripId,
                                stageId = id,
                                startLocationName = startLocationName
                            )
                        },
                        updateStage = {
                            val stageUiState = getStageUiState(tripId, id)
                            CoroutineScope(Dispatchers.Default).launch {
                                tripAndStageRepository.addUserStageAfterTripEnd(
                                    tripId = tripId,
                                    mode = stageUiState.mode,
                                    startDateTime = stageUiState.startDateTime,
                                    endDateTime = stageUiState.endDateTime,
                                    endLocation = stageUiState.endLocation.toLocation(stageUiState.endDateTime.toInstant(ZoneOffset.UTC).toEpochMilli()),
                                )
                            }
                        }
                    )
                )
            )
        )
    }

    private fun updateTripPurpose(tripUiState: TripUiState) {
        CoroutineScope(Dispatchers.Default).launch {
            tripAndStageRepository.updateTripPurpose(
                tripId = tripUiState.id,
                purpose = tripUiState.purpose
            )
        }
    }

    private fun updateTrip(tripId: Long) {
        val tripUiState = getTripUiState(tripId)
        updateTripPurpose(tripUiState)
        updateStages(tripUiState)
    }

    private fun GeoPoint.toLocation(time: Long): Location {
        val location = Location("osmdroid")
        location.latitude = this.latitude
        location.longitude = this.longitude
        location.time = time
        return location
    }
}