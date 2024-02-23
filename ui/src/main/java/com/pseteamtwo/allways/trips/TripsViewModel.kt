package com.pseteamtwo.allways.trips



import android.location.Address
import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.pseteamtwo.allways.map.addressToString

import com.pseteamtwo.allways.trip.Mode
import com.pseteamtwo.allways.trip.Purpose

import com.pseteamtwo.allways.trip.repository.TripAndStageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
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
import javax.inject.Inject


@HiltViewModel
class TripsViewModel @Inject constructor(private val tripAndStageRepository: TripAndStageRepository) : ViewModel() {
    private var _tripsUiState: MutableStateFlow<TripsUiState> = MutableStateFlow(TripsUiState(loading = true))
    val tripsUiState: StateFlow<TripsUiState> = _tripsUiState.asStateFlow()

    private var nextId: Long = 0

    private val geocoder: GeocoderNominatim = GeocoderNominatim(Locale.getDefault(), Configuration.getInstance().userAgentValue)

    init {
        viewModelScope.launch {
            tripAndStageRepository.observeAllTrips().collect {
                trips ->
                val tripUiStates: MutableList<TripUiState> = mutableListOf()
                val tripUiStateId = nextId
                for (trip in trips) {

                    val tripUiState = TripUiState(
                        id = tripUiStateId,
                        tripId = trip.id,
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
                        deleteTrip = {deleteTrip(trip.id)},
                        createStageUiStates = {createStageUiStates(trip.id)},
                        addStageUiStateBefore = {addStageUiStateBefore(trip.id)},
                        addStageUiStateAfter = {addStageUiStateAfter(trip.id)},
                        updateTrip = {updateTrip(trip.id)}
                    )
                    nextId++

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
                            tripUiStateId,
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

    fun addTrip(): TripUiState {
        val oldTripUiStates = _tripsUiState.value.tripUiStates

        val tripUiStateId = nextId
        val stageUiStateId: Int = 0

        val dateTime = LocalDateTime.now()

        val location = GeoPoint(0.0,0.0) //TODO: aktuelle Poition?
        val locationName = "-"

        val tripUiState = TripUiState(
            id = tripUiStateId,
            tripId = 0,
            stageUiStates = listOf(
                StageUiState(
                    id = stageUiStateId,
                    stageId = 0,
                    mode = Mode.NONE,
                    isFirstStageOfTrip = true,
                    isLastStageOfTrip = true,
                    startDateTime = dateTime,
                    endDateTime = dateTime,
                    startLocation = location,
                    endLocation = location,
                    startLocationName = locationName,
                    endLocationName = locationName,
                    getPreviousStageUiState = {return@StageUiState null},
                    getNextStageUiState = {return@StageUiState null},
                    setMode = {
                            mode: Mode ->
                        setStageUiStageMode(
                            tripUiStateId = tripUiStateId,
                            stageUiStateId = stageUiStateId,
                            mode = mode
                        )
                    },
                    setStartDate = {
                            date: LocalDate ->
                        setStageUiStageStartDate(
                            tripUiStateId = tripUiStateId,
                            stageUiStateId = stageUiStateId,
                            date = date
                        )
                    },
                    setEndDate = {
                            date: LocalDate ->
                        setStageUiStageEndDate(
                            tripUiStateId = tripUiStateId,
                            stageUiStateId = stageUiStateId,
                            date = date
                        )
                    },
                    setStartTime = {
                            hour: Int, minute: Int ->
                        setStageUiStateStartTime(
                            tripUiStateId = tripUiStateId,
                            stageUiStateId = stageUiStateId,
                            hour = hour,
                            minute = minute
                        )
                    },
                    setEndTime = {
                            hour: Int, minute: Int ->
                        setStageUiStateEndTime(
                            tripUiStateId = tripUiStateId,
                            stageUiStateId = stageUiStateId,
                            hour = hour,
                            minute = minute)
                    },
                    setStartLocation = {
                            geoPoint: GeoPoint ->
                        setStageUiStateStartLocation(
                            tripUiStateId = tripUiStateId,
                            stageUiStateId = stageUiStateId,
                            geoPoint = geoPoint
                        )
                    },
                    setEndLocation = {
                            geoPoint: GeoPoint ->
                        setStageUiStateEndLocation(
                            tripUiStateId = tripUiStateId,
                            stageUiStateId = stageUiStateId,
                            geoPoint = geoPoint
                        )
                    },
                    setStartLocationName = {
                            startLocationName: String ->
                        setStageUiStateStartLocationName(
                            tripUiStateId = tripUiStateId,
                            stageUiStateId = stageUiStateId,
                            startLocationName = startLocationName
                        )
                    },
                    setEndLocationName = {
                            endLocationName: String ->
                        setStageUiStateEndLocationName(
                            tripUiStateId = tripUiStateId,
                            stageUiStateId = stageUiStateId,
                            endLocationName = endLocationName
                        )
                    },
                    updateStage = {}
                ),
            ),
            purpose = Purpose.NONE,
            mode = Mode.NONE,
            isConfirmed = false,
            startDateTime = dateTime,
            endDateTime = dateTime,
            startLocation = location,
            endLocation = location,
            startLocationName = locationName,
            endLocationName = locationName,
            duration = 0,
            distance = 0,
            deleteTrip = {deleteTripUiState(tripUiStateId)},
            createStageUiStates = {},
            addStageUiStateBefore = {addStageUiStateBefore(tripUiStateId)},
            addStageUiStateAfter = {addStageUiStateAfter(tripUiStateId)},
            updateTrip = {
                val tripUiState = getTripUiState(tripUiStateId)
                viewModelScope.launch {
                    tripAndStageRepository.createTrip(
                        purpose =  tripUiState.purpose,
                        stages = emptyList()
                    )
                }
            }
        )

        _tripsUiState.update {
            it.copy(
                tripUiStates = (oldTripUiStates + tripUiState).sorted()
            )
        }
        nextId++

        return tripUiState
    }

    private fun deleteTrip(tripUiStateId: Long) {
        val tripUiState = getTripUiState(tripUiStateId)
        viewModelScope.launch {
            tripAndStageRepository.deleteTrip(tripUiState.tripId)
        }
    }

    private fun deleteTripUiState(tripUiStateId: Long) {
        val tripUiStateIndex = getTripUiStateIndex(tripUiStateId)
        _tripsUiState.update {
            it.copy(
                tripUiStates =
                    it.tripUiStates.slice(0 until tripUiStateIndex)
                    + it.tripUiStates.slice(tripUiStateIndex + 1 until it.tripUiStates.size)
            )
        }
    }

    private fun createStageUiStates(tripUiStateId: Long) {
        viewModelScope.launch {
            tripAndStageRepository.observeStagesOfTrip(tripUiStateId).collect {
                stages ->
                val tripUiStates = getTripUiStates()
                val tripUiStateIndex = getTripUiStateIndex(tripUiStateId)
                val tripUiState = tripUiStates[tripUiStateIndex]
                val stageUiStates: MutableList<StageUiState> = mutableListOf()
                for (i in stages.indices) {
                    val stage = stages[i]
                    val isFirstStageOfTrip = (i == 0)
                    val isLastStageOfTrip = (i == stages.lastIndex)

                    val stageUiState = StageUiState(
                        id = i,
                        stageId = stage.id,
                        mode = stage.mode,
                        isFirstStageOfTrip = isFirstStageOfTrip,
                        isLastStageOfTrip = isLastStageOfTrip,
                        startDateTime = stage.startDateTime,
                        endDateTime = stage.endDateTime,
                        startLocation = GeoPoint(stage.startLocation),
                        endLocation = GeoPoint(stage.endLocation),
                        startLocationName = "Lädt...",
                        endLocationName = "Lädt...",
                        getPreviousStageUiState = {
                            return@StageUiState if(isFirstStageOfTrip) null else getStageUiState(tripUiStateId, i - 1)
                        },
                        getNextStageUiState = {
                            return@StageUiState if(isLastStageOfTrip) null else getStageUiState(tripUiStateId, i + 1)
                        },
                        setMode = {
                            mode: Mode ->
                            setStageUiStageMode(
                                tripUiStateId = tripUiStateId,
                                stageUiStateId = i,
                                mode = mode
                            )
                        },
                        setStartDate = {
                            date: LocalDate ->
                            setStageUiStageStartDate(
                                tripUiStateId = tripUiStateId,
                                stageUiStateId = i,
                                date = date
                            )
                        },
                        setEndDate = {
                            date: LocalDate ->
                            setStageUiStageEndDate(
                                tripUiStateId = tripUiStateId,
                                stageUiStateId = i,
                                date = date
                            )
                        },
                        setStartTime = {
                            hour: Int, minute: Int ->
                            setStageUiStateStartTime(
                                tripUiStateId = tripUiStateId,
                                stageUiStateId = i,
                                hour = hour,
                                minute = minute
                            )
                        },
                        setEndTime = {
                            hour: Int, minute: Int ->
                            setStageUiStateEndTime(
                                tripUiStateId = tripUiStateId,
                                stageUiStateId = i,
                                hour = hour,
                                minute = minute)
                        },
                        setStartLocation = {
                            geoPoint: GeoPoint ->
                            setStageUiStateStartLocation(
                                tripUiStateId = tripUiStateId,
                                stageUiStateId = i,
                                geoPoint = geoPoint
                            )
                        },
                        setEndLocation = {
                            geoPoint: GeoPoint ->
                            setStageUiStateEndLocation(
                                tripUiStateId = tripUiStateId,
                                stageUiStateId = i,
                                geoPoint = geoPoint
                            )
                            if(!isLastStageOfTrip) {
                                setStageUiStateStartLocation(
                                    tripUiStateId = tripUiStateId,
                                    stageUiStateId = i + 1,
                                    geoPoint = geoPoint
                                )
                            }
                        },
                        setStartLocationName = {
                            startLocationName: String ->
                            setStageUiStateStartLocationName(
                                tripUiStateId = tripUiStateId,
                                stageUiStateId = i,
                                startLocationName = startLocationName
                            )
                        },
                        setEndLocationName = {
                            endLocationName: String ->
                            setStageUiStateEndLocationName(
                                tripUiStateId = tripUiStateId,
                                stageUiStateId = i,
                                endLocationName = endLocationName
                            )
                            if(!isLastStageOfTrip) {
                                setStageUiStateStartLocationName(
                                    tripUiStateId = tripUiStateId,
                                    stageUiStateId = i + 1,
                                    startLocationName = endLocationName
                                )
                            }
                        },
                        updateStage = {
                            updateStage(tripUiStateId = tripUiStateId, stageUiStateId = i)
                        }
                    )

                    stageUiStates.add(stageUiState)
                }

                updateTripUiState(
                    tripUiStateId = tripUiStateId,
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
                            tripUiStateId = tripUiStateId,
                            stageUiStateId = stageUiState.id,
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
    private fun getTripUiStateIndex(tripUiStateId: Long): Int {
        return getTripUiStates().indexOfFirst{it.id == tripUiStateId}
    }

    private fun getTripUiState(tripUiStateId: Long): TripUiState {
        return getTripUiStates()[getTripUiStateIndex(tripUiStateId)]
    }

    private fun getStageUiStates(tripUiStateId: Long): List<StageUiState> {
        return getTripUiState(tripUiStateId).stageUiStates
    }

    private fun getStageUiStateIndex(tripUiStateId: Long, stageUiStateId: Int): Int {
        return getStageUiStates(tripUiStateId).indexOfFirst{it.id == stageUiStateId}
    }

    private fun getStageUiState(tripUiStateId: Long, stageUiStateId: Int): StageUiState {
        return getStageUiStates(tripUiStateId)[getStageUiStateIndex(tripUiStateId, stageUiStateId)]
    }

    private fun updateTripUiState(tripUiStateId: Long, newTripUiState: TripUiState) {
        val tripUiStates = getTripUiStates()
        val tripUiStateIndex = getTripUiStateIndex(tripUiStateId)
        _tripsUiState.update {
            it.copy(
                tripUiStates =
                    tripUiStates.slice(0 until tripUiStateIndex)
                    + newTripUiState
                    + tripUiStates.slice(tripUiStateIndex + 1 until tripUiStates.size)
            )
        }
    }

    private fun updateStageUiState(tripUiStateId: Long, stageUiStateId: Int, newStageUiState: StageUiState) {
        val tripUiState = getTripUiState(tripUiStateId)
        val stageUiStates = getStageUiStates(tripUiStateId)
        val stageUiStateIndex = getStageUiStateIndex(tripUiStateId, stageUiStateId)

        updateTripUiState(
            tripUiStateId,
            tripUiState.copy(
                stageUiStates =
                    stageUiStates.slice(0 until stageUiStateIndex)
                    + newStageUiState
                    + stageUiStates.slice(stageUiStateIndex + 1 until stageUiStates.size)
            )
        )
    }

    private fun setStageUiStageMode(
        tripUiStateId: Long,
        stageUiStateId: Int,
        mode: Mode
    ) {
        updateStageUiState(
            tripUiStateId = tripUiStateId,
            stageUiStateId = stageUiStateId,
            newStageUiState = getStageUiState(tripUiStateId, stageUiStateId).copy(mode = mode)
        )
    }

    private fun setStageUiStageStartDate(
        tripUiStateId: Long,
        stageUiStateId: Int,
        date: LocalDate
    ) {
        val stageUiState = getStageUiState(tripUiStateId, stageUiStateId)
        updateStageUiState(
            tripUiStateId = tripUiStateId,
            stageUiStateId = stageUiStateId,
            newStageUiState = stageUiState.copy(
                startDateTime = LocalDateTime.of(date, stageUiState.startDateTime.toLocalTime())
            )
        )
    }

    private fun setStageUiStageEndDate(
        tripUiStateId: Long,
        stageUiStateId: Int,
        date: LocalDate
    ) {
        val stageUiState = getStageUiState(tripUiStateId, stageUiStateId)
        updateStageUiState(
            tripUiStateId = tripUiStateId,
            stageUiStateId = stageUiStateId,
            newStageUiState = stageUiState.copy(
                endDateTime = LocalDateTime.of(date, stageUiState.endDateTime.toLocalTime())
            )
        )
    }

    private fun setStageUiStateStartTime(
        tripUiStateId: Long,
        stageUiStateId: Int,
        hour: Int = getStageUiState(tripUiStateId, stageUiStateId).startDateTime.hour,
        minute: Int = getStageUiState(tripUiStateId, stageUiStateId).startDateTime.minute
    ) {
        val stageUiState = getStageUiState(tripUiStateId, stageUiStateId)
        val startDate = stageUiState.startDateTime.toLocalDate()
        val startTime = LocalTime.of(hour, minute)
        updateStageUiState(
            tripUiStateId = tripUiStateId,
            stageUiStateId = stageUiStateId,
            newStageUiState = stageUiState.copy(
                startDateTime = LocalDateTime.of(startDate, startTime)
            )
        )
    }

    private fun setStageUiStateEndTime(
        tripUiStateId: Long,
        stageUiStateId: Int,
        hour: Int = getStageUiState(tripUiStateId, stageUiStateId).endDateTime.hour,
        minute: Int = getStageUiState(tripUiStateId, stageUiStateId).endDateTime.minute
    ) {
        val stageUiState = getStageUiState(tripUiStateId, stageUiStateId)
        var endDate = stageUiState.endDateTime.toLocalDate()
        val endTime = LocalTime.of(hour, minute)
        if (endTime < stageUiState.startDateTime.toLocalTime()) {
            endDate = endDate.plusDays(1)
        }
        updateStageUiState(
            tripUiStateId = tripUiStateId,
            stageUiStateId = stageUiStateId,
            newStageUiState = stageUiState.copy(
                endDateTime = LocalDateTime.of(endDate, endTime)
            )
        )
    }

    private fun setStageUiStateStartLocation(
        tripUiStateId: Long,
        stageUiStateId: Int,
        geoPoint: GeoPoint
    ) {
        val stageUiState = getStageUiState(tripUiStateId, stageUiStateId)
        updateStageUiState(
            tripUiStateId = tripUiStateId,
            stageUiStateId = stageUiStateId,
            newStageUiState = stageUiState.copy(
                startLocation = geoPoint
            )
        )
    }

    private fun setStageUiStateEndLocation(
        tripUiStateId: Long,
        stageUiStateId: Int,
        geoPoint: GeoPoint
    ) {
        val stageUiState = getStageUiState(tripUiStateId, stageUiStateId)
        updateStageUiState(
            tripUiStateId = tripUiStateId,
            stageUiStateId = stageUiStateId,
            newStageUiState = stageUiState.copy(
                endLocation = geoPoint
            )
        )
    }

    private fun setStageUiStateStartLocationName(
        tripUiStateId: Long,
        stageUiStateId: Int,
        startLocationName: String
    ) {
        val stageUiState = getStageUiState(tripUiStateId, stageUiStateId)
        updateStageUiState(
            tripUiStateId = tripUiStateId,
            stageUiStateId = stageUiStateId,
            newStageUiState = stageUiState.copy(
                startLocationName = startLocationName
            )
        )
    }

    private fun setStageUiStateEndLocationName(
        tripUiStateId: Long,
        stageUiStateId: Int,
        endLocationName: String
    ) {
        val stageUiState = getStageUiState(tripUiStateId, stageUiStateId)
        updateStageUiState(
            tripUiStateId = tripUiStateId,
            stageUiStateId = stageUiStateId,
            newStageUiState = stageUiState.copy(
                endLocationName = endLocationName
            )
        )
    }

    private fun updateStage(
        tripUiStateId: Long,
        stageUiStateId: Int
    ) {
        val stageUiState = getStageUiState(tripUiStateId, stageUiStateId)
        viewModelScope.launch {
            tripAndStageRepository.updateStage(
                stageId = stageUiState.stageId,
                mode = stageUiState.mode,
                startDateTime = stageUiState.startDateTime,
                endDateTime = stageUiState.endDateTime,
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

    private fun addStageUiStateBefore(tripUiStateId: Long) {
        val tripUiState = getTripUiState(tripUiStateId)
        val stageUiStates = tripUiState.stageUiStates
        val firstStageUiState = stageUiStates.first()
        val firstStageUiStateId = firstStageUiState.id
        val stageUiStateId = firstStageUiStateId - 1
        val location = firstStageUiState.startLocation
        val startLocationName = "-"
        val endLocationName = firstStageUiState.startLocationName
        val dateTime = firstStageUiState.startDateTime
        updateTripUiState(
            tripUiStateId = tripUiStateId,
            newTripUiState = tripUiState.copy(
                stageUiStates =
                    emptyList<StageUiState>()
                    + StageUiState(
                        id = stageUiStateId,
                        stageId = 0,
                        mode = Mode.NONE,
                        isFirstStageOfTrip = true,
                        isLastStageOfTrip = false,
                        startDateTime = dateTime,
                        endDateTime = dateTime,
                        startLocation = location,
                        endLocation = location,
                        startLocationName = startLocationName,
                        endLocationName = endLocationName,
                        getPreviousStageUiState = {return@StageUiState null},
                        getNextStageUiState = {return@StageUiState getStageUiState(tripUiStateId, firstStageUiStateId)},
                        setMode = {
                            mode: Mode ->
                            setStageUiStageMode(
                                tripUiStateId = tripUiStateId,
                                stageUiStateId = stageUiStateId,
                                mode = mode
                            )
                        },
                        setStartDate = {
                            date: LocalDate ->
                            setStageUiStageStartDate(
                                tripUiStateId = tripUiStateId,
                                stageUiStateId = stageUiStateId,
                                date = date
                            )
                        },
                        setEndDate = {
                            date: LocalDate ->
                            setStageUiStageEndDate(
                                tripUiStateId = tripUiStateId,
                                stageUiStateId = stageUiStateId,
                                date = date
                            )
                        },
                        setStartTime = {
                            hour: Int, minute: Int ->
                            setStageUiStateStartTime(
                                tripUiStateId = tripUiStateId,
                                stageUiStateId = stageUiStateId,
                                hour = hour,
                                minute = minute
                            )
                        },
                        setEndTime = {
                            hour: Int, minute: Int ->
                            setStageUiStateEndTime(
                                tripUiStateId = tripUiStateId,
                                stageUiStateId = stageUiStateId,
                                hour = hour,
                                minute = minute)
                        },
                        setStartLocation = {
                            geoPoint: GeoPoint ->
                            setStageUiStateStartLocation(
                                tripUiStateId = tripUiStateId,
                                stageUiStateId = stageUiStateId,
                                geoPoint = geoPoint
                            )
                        },
                        setEndLocation = {
                            geoPoint: GeoPoint ->
                            setStageUiStateEndLocation(
                                tripUiStateId = tripUiStateId,
                                stageUiStateId = stageUiStateId,
                                geoPoint = geoPoint
                            )
                            setStageUiStateStartLocation(
                                tripUiStateId = tripUiStateId,
                                stageUiStateId = firstStageUiStateId,
                                geoPoint = geoPoint
                            )
                        },
                        setStartLocationName = {
                            locationName: String ->
                            setStageUiStateStartLocationName(
                                tripUiStateId = tripUiStateId,
                                stageUiStateId = stageUiStateId,
                                startLocationName = locationName
                            )
                        },
                        setEndLocationName = {
                            locationName: String ->
                            setStageUiStateEndLocationName(
                                tripUiStateId = tripUiStateId,
                                stageUiStateId = stageUiStateId,
                                endLocationName = locationName
                            )
                            setStageUiStateStartLocationName(
                                tripUiStateId = tripUiStateId,
                                stageUiStateId = firstStageUiStateId,
                                startLocationName = locationName
                            )
                        },
                        updateStage = {
                            val stageUiState = getStageUiState(tripUiStateId, stageUiStateId)
                            viewModelScope.launch {
                                tripAndStageRepository.addUserStageAfterTripEnd(
                                    tripId = tripUiStateId,
                                    mode = stageUiState.mode,
                                    startDateTime = stageUiState.startDateTime,
                                    endDateTime = stageUiState.endDateTime,
                                    endLocation = stageUiState.endLocation.toLocation(stageUiState.endDateTime.toInstant(ZoneOffset.UTC).toEpochMilli()),
                                )
                            }
                        }
                    )
                    + firstStageUiState.copy(
                        isFirstStageOfTrip = false,
                        getPreviousStageUiState = {return@copy getStageUiState(tripUiStateId, stageUiStateId)}
                    )
                    + stageUiStates.slice(1..stageUiStates.lastIndex )
            )
        )
    }

    private fun addStageUiStateAfter(tripUiStateId: Long) {
        val tripUiState = getTripUiState(tripUiStateId)
        val stageUiStates = tripUiState.stageUiStates
        val lastStageUiState = stageUiStates.last()
        val lastStageUiStateId = lastStageUiState.id
        val stageUiStateId = lastStageUiStateId + 1
        val location = lastStageUiState.endLocation
        val startLocationName = lastStageUiState.endLocationName
        val endLocationName = "-"
        val dateTime = lastStageUiState.endDateTime
        updateTripUiState(
            tripUiStateId = tripUiStateId,
            newTripUiState = tripUiState.copy(
                stageUiStates =
                    stageUiStates.slice(0 until stageUiStates.lastIndex)
                    + lastStageUiState.copy(
                        isLastStageOfTrip = false,
                        getNextStageUiState = {return@copy getStageUiState(tripUiStateId, stageUiStateId)},
                        setEndLocation = {
                            geoPoint: GeoPoint ->
                            setStageUiStateEndLocation(
                                tripUiStateId = tripUiStateId,
                                stageUiStateId = lastStageUiStateId,
                                geoPoint = geoPoint
                            )
                            setStageUiStateStartLocation(
                                tripUiStateId = tripUiStateId,
                                stageUiStateId = stageUiStateId,
                                geoPoint = geoPoint
                            )
                        },
                        setEndLocationName = {
                            locationName: String ->
                            setStageUiStateEndLocationName(
                                tripUiStateId = tripUiStateId,
                                stageUiStateId = lastStageUiStateId,
                                endLocationName = locationName
                            )
                            setStageUiStateStartLocationName(
                                tripUiStateId = tripUiStateId,
                                stageUiStateId = stageUiStateId,
                                startLocationName = locationName
                            )
                        }
                    )
                    + StageUiState(
                        id = stageUiStateId,
                        stageId = 0,
                        mode = Mode.NONE,
                        isFirstStageOfTrip = false,
                        isLastStageOfTrip = true,
                        startDateTime = dateTime,
                        endDateTime = dateTime,
                        startLocation = location,
                        endLocation = location,
                        startLocationName = startLocationName,
                        endLocationName = endLocationName,
                        getPreviousStageUiState = {return@StageUiState null},
                        getNextStageUiState = {return@StageUiState getStageUiState(tripUiStateId, lastStageUiStateId)},
                        setMode = {
                                mode: Mode ->
                            setStageUiStageMode(
                                tripUiStateId = tripUiStateId,
                                stageUiStateId = stageUiStateId,
                                mode = mode
                            )
                        },
                        setStartDate = {
                            date: LocalDate ->
                            setStageUiStageStartDate(
                                tripUiStateId = tripUiStateId,
                                stageUiStateId = stageUiStateId,
                                date = date
                            )
                        },
                        setEndDate = {
                            date: LocalDate ->
                            setStageUiStageEndDate(
                                tripUiStateId = tripUiStateId,
                                stageUiStateId = stageUiStateId,
                                date = date
                            )
                        },
                        setStartTime = {
                                hour: Int, minute: Int ->
                            setStageUiStateStartTime(
                                tripUiStateId = tripUiStateId,
                                stageUiStateId = stageUiStateId,
                                hour = hour,
                                minute = minute
                            )
                        },
                        setEndTime = {
                                hour: Int, minute: Int ->
                            setStageUiStateEndTime(
                                tripUiStateId = tripUiStateId,
                                stageUiStateId = stageUiStateId,
                                hour = hour,
                                minute = minute)
                        },
                        setStartLocation = {
                                geoPoint: GeoPoint ->
                            setStageUiStateStartLocation(
                                tripUiStateId = tripUiStateId,
                                stageUiStateId = stageUiStateId,
                                geoPoint = geoPoint
                            )
                        },
                        setEndLocation = {
                                geoPoint: GeoPoint ->
                            setStageUiStateEndLocation(
                                tripUiStateId = tripUiStateId,
                                stageUiStateId = stageUiStateId,
                                geoPoint = geoPoint
                            )
                        },
                        setStartLocationName = {
                                locationName: String ->
                            setStageUiStateStartLocationName(
                                tripUiStateId = tripUiStateId,
                                stageUiStateId = stageUiStateId,
                                startLocationName = locationName
                            )
                        },
                        setEndLocationName = {
                                locationName: String ->
                            setStageUiStateEndLocationName(
                                tripUiStateId = tripUiStateId,
                                stageUiStateId = stageUiStateId,
                                endLocationName = locationName
                            )
                        },
                        updateStage = {
                            val stageUiState = getStageUiState(tripUiStateId, stageUiStateId)
                            viewModelScope.launch {
                                tripAndStageRepository.addUserStageAfterTripEnd(
                                    tripId = tripUiStateId,
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
                tripId = tripUiState.tripId,
                purpose = tripUiState.purpose
            )
        }
    }

    private fun updateTrip(tripUiStateId: Long) {
        val tripUiState = getTripUiState(tripUiStateId)
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