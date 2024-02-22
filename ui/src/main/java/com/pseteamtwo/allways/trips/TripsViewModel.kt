package com.pseteamtwo.allways.trips



import android.location.Address
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
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.LocalTime
import org.threeten.bp.ZoneOffset
import java.io.IOException
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
                        mode = Mode.NONE, // TODO: Methode in Trip, die aus Stages das meistgenutze Verkerhsmittel für einen Trip bestimmt?
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
                        addStageUiStateBefore = {addStageUiStateBefore(trip.id)},
                        addStageUiStateAfter = {addStageUiStateAfter(trip.id)},
                        updateTrip = {updateTrip(trip.id)}
                    )

                    tripUiStates.add(tripUiState)

                    CoroutineScope(Dispatchers.IO).launch{
                        var addresses: List<Address>
                        var startLocationName: String
                        var endLocationName: String
                        try {
                            addresses = withContext(Dispatchers.IO) {geocoder.getFromLocation(trip.startLocation.latitude, trip.startLocation.longitude, 1)}
                            startLocationName = addressToString(addresses[0])
                        } catch (exception: IOException) {
                            startLocationName = "-"
                        }
                        try {
                            addresses = withContext(Dispatchers.IO) {geocoder.getFromLocation(trip.startLocation.latitude, trip.startLocation.longitude, 1)}
                            endLocationName = addressToString(addresses[0])
                        } catch (exception: IOException) {
                            endLocationName = "-"
                        }

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
                for (i in stages.indices) {
                    val stage = stages[i]
                    val isFirstStageOfTrip = (i == 0)
                    val isLastStageOfTrip = (i == stages.lastIndex)
                    val nextStage = if(!isLastStageOfTrip) stages[i+1] else null

                    val stageUiState = StageUiState(
                        id = stage.id,
                        mode = stage.mode,
                        isFirstStageOfTrip = isFirstStageOfTrip,
                        isLastStageOfTrip = isLastStageOfTrip,
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
                        setStartDate = {
                            date: LocalDate ->
                            setStageUiStageStartDate(
                                tripId = tripId,
                                stageId = stage.id,
                                date = date
                            )
                        },
                        setEndDate = {
                            date: LocalDate ->
                            setStageUiStageEndDate(
                                tripId = tripId,
                                stageId = stage.id,
                                date = date
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
                            if(!isLastStageOfTrip) {
                                setStageUiStateStartLocation(
                                    tripId = tripId,
                                    stageId = nextStage!!.id,
                                    geoPoint = geoPoint
                                )
                            }
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
                            endLocationName: String ->
                            setStageUiStateEndLocationName(
                                tripId = tripId,
                                stageId = stage.id,
                                endLocationName = endLocationName
                            )
                            if(!isLastStageOfTrip) {
                                setStageUiStateStartLocationName(
                                    tripId = tripId,
                                    stageId = nextStage!!.id,
                                    startLocationName = endLocationName
                                )
                            }
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
                    viewModelScope.launch{
                        var addresses: List<Address>
                        var startLocationName: String
                        var endLocationName: String
                        try {
                            addresses = withContext(Dispatchers.IO) {geocoder.getFromLocation(stageUiState.startLocation.latitude, stageUiState.startLocation.longitude, 1)}
                            startLocationName = addressToString(addresses[0])
                        } catch (exception: IOException) {
                            startLocationName = "-"
                        }
                        try {
                            addresses = withContext(Dispatchers.IO) {geocoder.getFromLocation(stageUiState.startLocation.latitude, stageUiState.startLocation.longitude, 1)}
                            endLocationName = addressToString(addresses[0])
                        } catch (exception: IOException) {
                            endLocationName = "-"
                        }

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

    private fun setStageUiStageStartDate(
        tripId: Long,
        stageId: Long,
        date: LocalDate
    ) {
        val stageUiState = getStageUiState(tripId, stageId)
        updateStageUiState(
            tripId = tripId,
            stageId = stageId,
            newStageUiState = stageUiState.copy(
                startDateTime = LocalDateTime.of(date, stageUiState.startDateTime.toLocalTime())
            )
        )
    }

    private fun setStageUiStageEndDate(
        tripId: Long,
        stageId: Long,
        date: LocalDate
    ) {
        val stageUiState = getStageUiState(tripId, stageId)
        updateStageUiState(
            tripId = tripId,
            stageId = stageId,
            newStageUiState = stageUiState.copy(
                endDateTime = LocalDateTime.of(date, stageUiState.endDateTime.toLocalTime())
            )
        )
    }

    private fun setStageUiStateStartTime(
        tripId: Long,
        stageId: Long,
        hour: Int = getStageUiState(tripId, stageId).startDateTime.hour,
        minute: Int = getStageUiState(tripId, stageId).startDateTime.minute
    ) {
        val stageUiState = getStageUiState(tripId, stageId)
        val startDate = stageUiState.startDateTime.toLocalDate()
        val startTime = LocalTime.of(hour, minute)
        updateStageUiState(
            tripId = tripId,
            stageId = stageId,
            newStageUiState = stageUiState.copy(
                startDateTime = LocalDateTime.of(startDate, startTime)
            )
        )
    }

    private fun setStageUiStateEndTime(
        tripId: Long,
        stageId: Long,
        hour: Int = getStageUiState(tripId, stageId).endDateTime.hour,
        minute: Int = getStageUiState(tripId, stageId).endDateTime.minute
    ) {
        val stageUiStates = getStageUiStates(tripId)
        val stageUiState = getStageUiState(tripId, stageId)
        var endDate = stageUiState.endDateTime.toLocalDate()
        val endTime = LocalTime.of(hour, minute)
        if (endTime < stageUiState.startDateTime.toLocalTime()) {
            endDate = endDate.plusDays(1)
        }
        updateStageUiState(
            tripId = tripId,
            stageId = stageId,
            newStageUiState = stageUiState.copy(
                endDateTime = LocalDateTime.of(endDate, endTime)
            )
        )
        for(i in getTripUiStateIndex(stageId) + 1 until stageUiStates.lastIndex) {
            stageUiStates[i].setStartDate(stageUiState.startDateTime.toLocalDate().plusDays(1))
            stageUiStates[i].setEndDate(stageUiState.endDateTime.toLocalDate().plusDays(1))
        }
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
        endLocationName: String
    ) {
        val stageUiState = getStageUiState(tripId, stageId)
        updateStageUiState(
            tripId = tripId,
            stageId = stageId,
            newStageUiState = stageUiState.copy(
                endLocationName = endLocationName
            )
        )
    }

    private fun updateStage(
        tripId: Long,
        stageId: Long
    ) {
        val stageUiState = getStageUiState(tripId, stageId)
        viewModelScope.launch {
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

    private fun addStageUiStateBefore(tripId: Long) {
        val tripUiState = getTripUiState(tripId)
        val stageUiStates = tripUiState.stageUiStates
        val firstStageUiState = stageUiStates.first()
        val firstStageId = firstStageUiState.id
        val stageId = firstStageId + 1
        val location = firstStageUiState.startLocation
        val startLocationName = "-"
        val endLocationName = firstStageUiState.startLocationName
        val dateTime = firstStageUiState.startDateTime
        updateTripUiState(
            tripId = tripId,
            newTripUiState = tripUiState.copy(
                stageUiStates =
                    emptyList<StageUiState>()
                    + StageUiState(
                        id = stageId,
                        mode = Mode.NONE,
                        isFirstStageOfTrip = true,
                        isLastStageOfTrip = false,
                        startDateTime = dateTime,
                        endDateTime = dateTime,
                        startLocation = location,
                        endLocation = location,
                        startLocationName = startLocationName,
                        endLocationName = endLocationName,
                        setMode = {
                            mode: Mode ->
                            setStageUiStageMode(
                                tripId = tripId,
                                stageId = stageId,
                                mode = mode
                            )
                        },
                        setStartDate = {
                            date: LocalDate ->
                            setStageUiStageStartDate(
                                tripId = tripId,
                                stageId = stageId,
                                date = date
                            )
                        },
                        setEndDate = {
                            date: LocalDate ->
                            setStageUiStageEndDate(
                                tripId = tripId,
                                stageId = stageId,
                                date = date
                            )
                        },
                        setStartTime = {
                            hour: Int, minute: Int ->
                            setStageUiStateStartTime(
                                tripId = tripId,
                                stageId = stageId,
                                hour = hour,
                                minute = minute
                            )
                        },
                        setEndTime = {
                            hour: Int, minute: Int ->
                            setStageUiStateEndTime(
                                tripId = tripId,
                                stageId = stageId,
                                hour = hour,
                                minute = minute)
                        },
                        setStartLocation = {
                            geoPoint: GeoPoint ->
                            setStageUiStateStartLocation(
                                tripId = tripId,
                                stageId = stageId,
                                geoPoint = geoPoint
                            )
                        },
                        setEndLocation = {
                            geoPoint: GeoPoint ->
                            setStageUiStateEndLocation(
                                tripId = tripId,
                                stageId = stageId,
                                geoPoint = geoPoint
                            )
                            setStageUiStateStartLocation(
                                tripId = tripId,
                                stageId = firstStageId,
                                geoPoint = geoPoint
                            )
                        },
                        setStartLocationName = {
                            locationName: String ->
                            setStageUiStateStartLocationName(
                                tripId = tripId,
                                stageId = stageId,
                                startLocationName = locationName
                            )
                        },
                        setEndLocationName = {
                            locationName: String ->
                            setStageUiStateEndLocationName(
                                tripId = tripId,
                                stageId = stageId,
                                endLocationName = locationName
                            )
                            setStageUiStateStartLocationName(
                                tripId = tripId,
                                stageId = firstStageId,
                                startLocationName = locationName
                            )
                        },
                        updateStage = {
                            val stageUiState = getStageUiState(tripId, stageId)
                            viewModelScope.launch {
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
                    + firstStageUiState.copy(
                        isFirstStageOfTrip = false
                    )
                    + stageUiStates.slice(1..stageUiStates.lastIndex )
            )
        )
    }

    private fun addStageUiStateAfter(tripId: Long) {
        val tripUiState = getTripUiState(tripId)
        val stageUiStates = tripUiState.stageUiStates
        val lastStageUiState = stageUiStates.last()
        val lastStageId = lastStageUiState.id
        val stageId = lastStageId + 1
        val location = lastStageUiState.endLocation
        val startLocationName = lastStageUiState.endLocationName
        val endLocationName = "-"
        val dateTime = lastStageUiState.endDateTime
        updateTripUiState(
            tripId = tripId,
            newTripUiState = tripUiState.copy(
                stageUiStates =
                    stageUiStates.slice(0 until stageUiStates.lastIndex)
                    + lastStageUiState.copy(
                        isLastStageOfTrip = false,
                        setEndLocation = {
                            geoPoint: GeoPoint ->
                            setStageUiStateEndLocation(
                                tripId = tripId,
                                stageId = lastStageId,
                                geoPoint = geoPoint
                            )
                            setStageUiStateStartLocation(
                                tripId = tripId,
                                stageId = stageId,
                                geoPoint = geoPoint
                            )
                        },
                        setEndLocationName = {
                            locationName: String ->
                            setStageUiStateEndLocationName(
                                tripId = tripId,
                                stageId = lastStageId,
                                endLocationName = locationName
                            )
                            setStageUiStateStartLocationName(
                                tripId = tripId,
                                stageId = stageId,
                                startLocationName = locationName
                            )
                        }
                    )
                    + StageUiState(
                        id = stageId,
                        mode = Mode.NONE,
                        isFirstStageOfTrip = false,
                        isLastStageOfTrip = true,
                        startDateTime = dateTime,
                        endDateTime = dateTime,
                        startLocation = location,
                        endLocation = location,
                        startLocationName = startLocationName,
                        endLocationName = endLocationName,
                        setMode = {
                                mode: Mode ->
                            setStageUiStageMode(
                                tripId = tripId,
                                stageId = stageId,
                                mode = mode
                            )
                        },
                        setStartDate = {
                            date: LocalDate ->
                            setStageUiStageStartDate(
                                tripId = tripId,
                                stageId = stageId,
                                date = date
                            )
                        },
                        setEndDate = {
                            date: LocalDate ->
                            setStageUiStageEndDate(
                                tripId = tripId,
                                stageId = stageId,
                                date = date
                            )
                        },
                        setStartTime = {
                                hour: Int, minute: Int ->
                            setStageUiStateStartTime(
                                tripId = tripId,
                                stageId = stageId,
                                hour = hour,
                                minute = minute
                            )
                        },
                        setEndTime = {
                                hour: Int, minute: Int ->
                            setStageUiStateEndTime(
                                tripId = tripId,
                                stageId = stageId,
                                hour = hour,
                                minute = minute)
                        },
                        setStartLocation = {
                                geoPoint: GeoPoint ->
                            setStageUiStateStartLocation(
                                tripId = tripId,
                                stageId = stageId,
                                geoPoint = geoPoint
                            )
                        },
                        setEndLocation = {
                                geoPoint: GeoPoint ->
                            setStageUiStateEndLocation(
                                tripId = tripId,
                                stageId = stageId,
                                geoPoint = geoPoint
                            )
                        },
                        setStartLocationName = {
                                locationName: String ->
                            setStageUiStateStartLocationName(
                                tripId = tripId,
                                stageId = stageId,
                                startLocationName = locationName
                            )
                        },
                        setEndLocationName = {
                                locationName: String ->
                            setStageUiStateEndLocationName(
                                tripId = tripId,
                                stageId = stageId,
                                endLocationName = locationName
                            )
                        },
                        updateStage = {
                            val stageUiState = getStageUiState(tripId, stageId)
                            viewModelScope.launch {
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
    }

    private fun updateTripPurpose(tripUiState: TripUiState) {
        viewModelScope.launch {
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