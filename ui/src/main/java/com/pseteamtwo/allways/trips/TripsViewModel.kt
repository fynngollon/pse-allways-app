package com.pseteamtwo.allways.trips



import android.location.Address
import android.location.Location

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.pseteamtwo.allways.map.addressToString
import com.pseteamtwo.allways.trip.GpsPoint
import com.pseteamtwo.allways.trip.Mode
import com.pseteamtwo.allways.trip.Purpose
import com.pseteamtwo.allways.trip.Stage
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

import java.io.IOException
import java.util.Locale

import javax.inject.Inject

/**
 * View model holding the [TripsScreen's][TripsScreen] UI state. Converts trip data from a
 * [TripAndStageRepository] into a [TripsUiState] and passes user input back.
 *
 * @property tripsUiState the current TripUiState
 * */
@HiltViewModel
class TripsViewModel @Inject constructor(private val tripAndStageRepository: TripAndStageRepository) : ViewModel() {
    private var _tripsUiState: MutableStateFlow<TripsUiState> = MutableStateFlow(TripsUiState())
    val tripsUiState: StateFlow<TripsUiState> = _tripsUiState.asStateFlow()

    private var nextTripUiStateId: Long = 0

    private val geocoder: GeocoderNominatim = GeocoderNominatim(Locale.getDefault(), Configuration.getInstance().userAgentValue)

    init {
        viewModelScope.launch {
            tripAndStageRepository.observeAllTrips().collect {
                trips ->
                val tripUiStates: MutableList<TripUiState> = mutableListOf()
                val tripUiStateId = nextTripUiStateId
                for (trip in trips) {

                    val tripUiState = TripUiState(
                        id = tripUiStateId,
                        tripId = trip.id,
                        stageUiStates = emptyList(),
                        purpose =  trip.purpose,
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
                        createStageUiStates = {createStageUiStates(tripUiStateId)},
                        addStageUiStateBefore = {addStageUiStateBefore(tripUiStateId)},
                        addStageUiStateAfter = {addStageUiStateAfter(tripUiStateId)},
                        setPurpose = {purpose: Purpose ->  setTripUiStatePurpose(tripUiStateId, purpose)},
                        updateTrip = {updateTrip(trip.id)},
                        sendToServer = false
                    )
                    nextTripUiStateId++

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
                    )
                }
            }
        }
    }

    fun addTrip(): TripUiState {
        val oldTripUiStates = _tripsUiState.value.tripUiStates

        val tripUiStateId = nextTripUiStateId
        val stageUiStateId: Int = 0

        val dateTime = LocalDateTime.now()

        val location = GeoPoint(49.009592,8.41512) //TODO: aktuelle Poition?
        val locationName = "-"

        val tripUiState = TripUiState(
            id = tripUiStateId,
            tripId = 0,
            stageUiStates = listOf(
                StageUiState(
                    id = stageUiStateId,
                    stageId = 0L,
                    mode = Mode.NONE,
                    isInDatabase = true,
                    isToBeAddedBefore = false,
                    isFirstStageOfTrip = true,
                    isLastStageOfTrip = true,
                    startDateTime = dateTime.minusMinutes(1),
                    endDateTime = dateTime,
                    startLocation = GeoPoint(49.009592,8.41512),
                    endLocation = GeoPoint(49.009592,8.41513),
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
                ),
            ),
            purpose = Purpose.NONE,
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
            setPurpose = {purpose: Purpose -> setTripUiStatePurpose(tripUiStateId, purpose)},
            updateTrip = {
                val tripUiState = getTripUiState(tripUiStateId)
                viewModelScope.launch {
                    val stages: MutableList<Stage> = mutableListOf()
                    for(stageUiState in getStageUiStates(tripUiStateId)) {
                        stages.add(
                            Stage(
                                id = 0L,
                                mode = stageUiState.mode,
                                gpsPoints = listOf(
                                    GpsPoint(
                                        id = 0L,
                                        geoPoint = stageUiState.startLocation,
                                        time = stageUiState.startDateTime
                                    ),
                                    GpsPoint(
                                        id = 0L,
                                        geoPoint = stageUiState.endLocation,
                                        time = stageUiState.endDateTime
                                    )
                                )
                            )
                        )
                    }
                    tripAndStageRepository.createTrip(
                        stages = stages,
                        purpose =  tripUiState.purpose
                    )
                }
            },
            sendToServer = false

        )

        _tripsUiState.update {
            it.copy(
                tripUiStates = (oldTripUiStates + tripUiState).sorted()
            )
        }
        nextTripUiStateId++

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
               /* val tripUiStates = getTripUiStates()
                val tripUiStateIndex = getTripUiStateIndex(tripUiStateId)*/
                val tripUiState = getTripUiState(tripUiStateId)
                val stageUiStates: MutableList<StageUiState> = mutableListOf()
                for (i in stages.indices) {
                    val stage = stages[i]
                    val isFirstStageOfTrip = (i == 0)
                    val isLastStageOfTrip = (i == stages.lastIndex)

                    val stageUiState = StageUiState(
                        id = i,
                        stageId = stage.id,
                        mode = stage.mode,
                        isInDatabase = true,
                        isToBeAddedBefore = false,
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

    private fun setTripUiStatePurpose(
        tripUiStateId: Long,
        purpose: Purpose
    ) {
        updateTripUiState(
            tripUiStateId = tripUiStateId,
            newTripUiState = getTripUiState(tripUiStateId).copy(purpose = purpose)
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

    private fun updateStages(tripUiState: TripUiState) {

        val stageIds: MutableList<Long> = mutableListOf()
        val modes: MutableList<Mode> = mutableListOf()
        val startDateTimes: MutableList<LocalDateTime> = mutableListOf()
        val endDateTimes: MutableList<LocalDateTime> = mutableListOf()
        val startLocations: MutableList<GeoPoint> = mutableListOf()
        val endLocations: MutableList<GeoPoint> = mutableListOf()

        tripUiState.stageUiStates.filter{ it.isInDatabase }.forEach {
            if(it.isInDatabase) {
                stageIds.add(it.stageId)
                modes.add(it.mode)
                startDateTimes.add(it.endDateTime)
                endDateTimes.add(it.endDateTime)
                startLocations.add(it.startLocation)
                endLocations.add(it.endLocation)
            }
        }

        viewModelScope.launch {
            tripAndStageRepository.updateStagesOfTrip(
                tripId = tripUiState.tripId,
                stageIds = stageIds,
                modes = modes,
                startDateTimes = startDateTimes,
                endDateTimes = endDateTimes,
                startLocations = startLocations,
                endLocations = endLocations,
            )
        }

        tripUiState.stageUiStates.filter{ !it.isInDatabase }.forEach{
            viewModelScope.launch {
                if(it.isToBeAddedBefore) {
                    tripAndStageRepository.addUserStageBeforeTripStart(
                        tripId = tripUiState.id,
                        mode = it.mode,
                        startDateTime = it.startDateTime,
                        endDateTime = it.endDateTime,
                        startLocation = it.startLocation.toLocation(it.startDateTime.toInstant(org.threeten.bp.ZoneId.systemDefault().rules.getOffset(org.threeten.bp.Instant.now())).toEpochMilli()),
                    )
                } else {
                    tripAndStageRepository.addUserStageAfterTripEnd(
                        tripId = tripUiState.id,
                        mode = it.mode,
                        startDateTime = it.startDateTime,
                        endDateTime = it.endDateTime,
                        endLocation = it.endLocation.toLocation(it.endDateTime.toInstant(org.threeten.bp.ZoneId.systemDefault().rules.getOffset(org.threeten.bp.Instant.now())).toEpochMilli()),
                    )
                }
            }
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
                        isInDatabase = false,
                        isToBeAddedBefore = true,
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
                        isInDatabase = false,
                        isToBeAddedBefore = false,
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

    private fun donateTrips(trips: List<TripUiState>) {
        viewModelScope.launch {
            val tripsToSend: MutableList<String> = mutableListOf()
            for (trip in trips) {
                tripsToSend.add(trip.tripId.toString())
            }
            tripAndStageRepository.saveTripsAndStagesToNetwork(tripsToSend)
        }
    }
}