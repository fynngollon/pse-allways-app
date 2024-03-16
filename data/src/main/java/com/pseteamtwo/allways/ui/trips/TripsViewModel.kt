package com.pseteamtwo.allways.ui.trips



import android.location.Location

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.pseteamtwo.allways.data.trip.GpsPoint

import com.pseteamtwo.allways.data.trip.Mode
import com.pseteamtwo.allways.data.trip.Purpose
import com.pseteamtwo.allways.data.trip.Stage

import com.pseteamtwo.allways.data.trip.repository.TripAndStageRepository

import dagger.hilt.android.lifecycle.HiltViewModel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.osmdroid.util.GeoPoint
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.LocalTime

import javax.inject.Inject

/**
 * View model holding the [TripsScreen's][TripsScreen] UI state. Converts trip data from a
 * [TripAndStageRepository] into a [TripsUiState] and passes user input back.
 *
 * @property tripsUiState the current TripUiState
 *
 * @see TripsScreen
 * @see TripAndStageRepository
 * @see TripsUiState
 * @see TripUiState
 * */
@HiltViewModel
class TripsViewModel @Inject constructor(private val tripAndStageRepository: TripAndStageRepository) : ViewModel() {
    //actual mutable UI state
    private var _tripsUiState: MutableStateFlow<TripsUiState> = MutableStateFlow(TripsUiState())

    //public immutable version of the UI state which can be observed by other classes
    val tripsUiState: StateFlow<TripsUiState> = _tripsUiState.asStateFlow()

    //ID for next TripUiState
    private var nextTripUiStateId: Long = 0

    //runs after initialization
    init {
        viewModelScope.launch {
            tripAndStageRepository.observeAllTrips().collect { trips ->
                val tripUiStates: MutableList<TripUiState> = mutableListOf()

                for (trip in trips) {
                    val tripUiStateId = nextTripUiStateId
                    //create tripUiState
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
                        startLocationName = "Loading...",
                        endLocationName = "Loading...",
                        duration = trip.duration,
                        distance = trip.distance,
                        deleteTrip = {deleteTrip(tripUiStateId)},
                        createStageUiStates = {createStageUiStates(tripUiStateId)},
                        addStageUiStateBefore = {addStageUiStateBefore(tripUiStateId)},
                        addStageUiStateAfter = {addStageUiStateAfter(tripUiStateId)},
                        setPurpose = {
                            purpose: Purpose ->  setTripUiStatePurpose(tripUiStateId, purpose)
                        },
                        updateTrip = {

                            var errorHasOccurred = false
                            var errorMessage = ""

                            try {
                                updateTrip(tripUiStateId)
                            } catch (exception: Exception) {
                                errorHasOccurred = true
                                errorMessage = exception.message.toString()
                            }

                            if(errorHasOccurred) {
                                throw IllegalArgumentException(errorMessage)
                            }
                        },
                        sendToServer = false
                    )

                    nextTripUiStateId++

                    //add to list
                    tripUiStates.add(tripUiState)
                }

                //update UI state
                _tripsUiState.update {
                    it.copy(
                        tripUiStates = tripUiStates,
                    )
                }
            }
        }
    }

    /**
     * Creates a new [TripsUiState]
     *
     * @return the ID of the new TripUiState
     * */
    fun addTrip(): Long {
        val oldTripUiStates = _tripsUiState.value.tripUiStates

        val tripUiStateId = nextTripUiStateId
        val stageUiStateId = 0

        val dateTime = LocalDateTime.now()

        val location = GeoPoint(49.009592,8.41512)
        val locationName = "-"

        val tripUiState = TripUiState(
            id = tripUiStateId,
            tripId = 0L,
            stageUiStates = listOf(
                StageUiState(
                    id = stageUiStateId,
                    stageId = 0L,
                    mode = Mode.NONE,
                    isInDatabase = true,
                    isToBeAddedBefore = false,
                    isFirstStageOfTrip = true,
                    isLastStageOfTrip = true,
                    startDateTime = dateTime.minusMinutes(2),
                    endDateTime = dateTime.minusMinutes(1),
                    startLocation = GeoPoint(49.009592,8.41512),
                    endLocation = GeoPoint(49.009592,8.41513),
                    startLocationName = locationName,
                    endLocationName = locationName,
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

                var errorHasOccurred = false
                var errorMessage = ""

                viewModelScope.launch() {
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
                    try {
                        tripAndStageRepository.createTrip(
                            stages = stages,
                            purpose =  tripUiState.purpose
                        )
                    // using Exception here because catching multiple exceptions like in Java
                    // ( e.g. catch(RuntimeException | IllegalArgumentException e) ) is not possible
                    // in Kotlin and I wanted to avoid multiple catch-blocks with the same code
                    } catch (exception: Exception) {
                        errorHasOccurred = true
                        errorMessage = exception.message!!
                    }
                }

                if (errorHasOccurred) {
                    throw IllegalArgumentException(errorMessage)
                }
            },
            sendToServer = false
        )

        nextTripUiStateId++

        _tripsUiState.update {
            it.copy(
                tripUiStates = (oldTripUiStates + tripUiState).sorted()
            )
        }

        return tripUiStateId
    }

    /**
     * Deletes the trip which belongs to the TripUiState with the specified [tripUiStateId] from the
     * database
     *
     * @param tripUiStateId the ID of the TripUiState
     * */
    private fun deleteTrip(tripUiStateId: Long) {
        val tripUiState = getTripUiState(tripUiStateId)
        viewModelScope.launch {
            tripAndStageRepository.deleteTrip(tripUiState.tripId)
        }
    }

    /**
     * Deletes the TripUiState with the specified [tripUiStateId]
     *
     * @param tripUiStateId the ID of the TripUiState
     * */
    fun deleteTripUiState(tripUiStateId: Long) {
        val tripUiStateIndex = getTripUiStateIndex(tripUiStateId)
        _tripsUiState.update {
            it.copy(
                tripUiStates =
                    it.tripUiStates.slice(0 until tripUiStateIndex)
                    + it.tripUiStates.slice(tripUiStateIndex + 1 until it.tripUiStates.size)
            )
        }
    }

    /**
     * Creates StageUiStates for them and adds them to the TripUiState with the specified
     * [tripUiStateId]
     *
     * @param tripUiStateId the ID of the TripUiState
     * */
    private fun createStageUiStates(tripUiStateId: Long) {
        val tripUiState = getTripUiState(tripUiStateId)
        viewModelScope.launch {
            tripAndStageRepository.observeStagesOfTrip(tripUiState.tripId).collect { stages ->
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
                        startLocationName = "Loading...",
                        endLocationName = "Loading...",
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
            }
        }
    }

    /**
     * Gets the list of all TripUiStates
     * */
    private fun getTripUiStates(): List<TripUiState> {
        return _tripsUiState.value.tripUiStates
    }

    /**
     * Gets the index of the TripUiState with the specified [tripUiStateId]
     *
     * @param tripUiStateId the ID of the TripUiState
     * */
    private fun getTripUiStateIndex(tripUiStateId: Long): Int {
        return getTripUiStates().indexOfFirst{it.id == tripUiStateId}
    }

    /**
     * Gets the TripUiState with the specified [tripUiStateId]
     *
     * @param tripUiStateId the ID of the TripUiState
     * */
    fun getTripUiState(tripUiStateId: Long): TripUiState {
        return getTripUiStates()[getTripUiStateIndex(tripUiStateId)]
    }


    /**
     * Gets the list of StageUiStates of the TripUiState with the specified [tripUiStateId]
     *
     * @param tripUiStateId the ID of the TripUiState
     * */
    private fun getStageUiStates(tripUiStateId: Long): List<StageUiState> {
        return getTripUiState(tripUiStateId).stageUiStates
    }

    /**
     * Gets the index of the StageUiState with the specified [stageUiStateId] belonging to the
     * TripUiState with the [tripUiStateId]
     *
     * @param tripUiStateId the ID of the TripUiState
     * @param stageUiStateId the ID of the StageUiState
     * */
    private fun getStageUiStateIndex(tripUiStateId: Long, stageUiStateId: Int): Int {
        return getStageUiStates(tripUiStateId).indexOfFirst{it.id == stageUiStateId}
    }

    /**
     * Gets the StageUiState with the specified [stageUiStateId] and [tripUiStateId]
     *
     * @param tripUiStateId the ID of the TripUiState
     * @param stageUiStateId the ID of the StageUiState
     * */
    private fun getStageUiState(tripUiStateId: Long, stageUiStateId: Int): StageUiState {
        return getStageUiStates(tripUiStateId)[getStageUiStateIndex(tripUiStateId, stageUiStateId)]
    }

    /**
     * Replaces the TripUiState with the specified [tripUiStateId] with the [newTripUiState]
     *
     * @param tripUiStateId the ID of the TripUiState
     * @param newTripUiState the new TripUiState
     * */
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

    /**
     * Replaces the StageUiState with the specified [stageUiStateId] belonging to the TripUiState
     * with the specified [tripUiStateId] with the [newStageUiState]
     *
     * @param stageUiStateId the ID of the StageUiState
     * @param tripUiStateId the ID of the TripUiState
     * @param newStageUiState the new StageUiState
     * */
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

    /**
     * Sets the purpose of the TripUiState with the specified [tripUiStateId] to the specified
     * [purpose]
     *
     * @param tripUiStateId the ID of the TripUiState
     * @param purpose the purpose to be set
     * */
    fun setTripUiStatePurpose(
        tripUiStateId: Long,
        purpose: Purpose
    ) {
        updateTripUiState(
            tripUiStateId = tripUiStateId,
            newTripUiState = getTripUiState(tripUiStateId).copy(purpose = purpose)
        )
    }

     fun setStageUiStageMode(
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

     fun setStageUiStageStartDate(
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

     fun setStageUiStageEndDate(
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

     fun setStageUiStateStartTime(
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

     fun setStageUiStateEndTime(
        tripUiStateId: Long,
        stageUiStateId: Int,
        hour: Int = getStageUiState(tripUiStateId, stageUiStateId).endDateTime.hour,
        minute: Int = getStageUiState(tripUiStateId, stageUiStateId).endDateTime.minute
    ) {
        val stageUiState = getStageUiState(tripUiStateId, stageUiStateId)
        val endDate = stageUiState.endDateTime.toLocalDate()
        val endTime = LocalTime.of(hour, minute)
        updateStageUiState(
            tripUiStateId = tripUiStateId,
            stageUiStateId = stageUiStateId,
            newStageUiState = stageUiState.copy(
                endDateTime = LocalDateTime.of(endDate, endTime)
            )
        )
    }

     fun setStageUiStateStartLocation(
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

     fun setStageUiStateEndLocation(
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

     fun setStageUiStateStartLocationName(
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

     fun setStageUiStateEndLocationName(
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

        var errorHasOccurred = false
        var errorMessage = ""

        val stageIds: MutableList<Long> = mutableListOf()
        val modes: MutableList<Mode> = mutableListOf()
        val startDateTimes: MutableList<LocalDateTime> = mutableListOf()
        val endDateTimes: MutableList<LocalDateTime> = mutableListOf()
        val startLocations: MutableList<GeoPoint> = mutableListOf()
        val endLocations: MutableList<GeoPoint> = mutableListOf()

        tripUiState.stageUiStates.filter{ it.isInDatabase }.forEach {
            stageIds.add(it.stageId)
            modes.add(it.mode)
            startDateTimes.add(it.startDateTime)
            endDateTimes.add(it.endDateTime)
            startLocations.add(it.startLocation)
            endLocations.add(it.endLocation)
        }

        runBlocking {
            try {
                tripAndStageRepository.updateStagesOfTrip(
                    tripId = tripUiState.tripId,
                    stageIds = stageIds,
                    modes = modes,
                    startDateTimes = startDateTimes,
                    endDateTimes = endDateTimes,
                    startLocations = startLocations,
                    endLocations = endLocations
                )
                // using Exception here because catching multiple exceptions like in Java
                // ( e.g. catch(RuntimeException | IllegalArgumentException e) ) is not possible
                // in Kotlin and I wanted to avoid multiple catch-blocks with the same code
            } catch (exception: Exception) {
                errorHasOccurred = true
                errorMessage = exception.message!!
            }
        }

        tripUiState.stageUiStates.filter{ !it.isInDatabase }.forEach{
            viewModelScope.launch {
                if(it.isToBeAddedBefore) {
                    try {
                        tripAndStageRepository.addUserStageBeforeTripStart(
                            tripId = tripUiState.tripId,
                            mode = it.mode,
                            startDateTime = it.startDateTime,
                            endDateTime = it.endDateTime,
                            startLocation = it.startLocation.toLocation(it.startDateTime.toInstant(org.threeten.bp.ZoneId.systemDefault().rules.getOffset(org.threeten.bp.Instant.now())).toEpochMilli()),
                        )
                    } catch (exception: Exception) {
                        errorHasOccurred = true
                        errorMessage = exception.message!!
                    }

                } else {
                    try {
                        tripAndStageRepository.addUserStageAfterTripEnd(
                            tripId = tripUiState.tripId,
                            mode = it.mode,
                            startDateTime = it.startDateTime,
                            endDateTime = it.endDateTime,
                            endLocation = it.endLocation.toLocation(it.endDateTime.toInstant(org.threeten.bp.ZoneId.systemDefault().rules.getOffset(org.threeten.bp.Instant.now())).toEpochMilli()),
                        )
                    } catch (exception: Exception) {
                        errorHasOccurred = true
                        errorMessage = exception.message!!
                    }
                }
            }
        }

        if(errorHasOccurred) {
            throw IllegalArgumentException(errorMessage)
        }
    }

     fun addStageUiStateBefore(tripUiStateId: Long) {
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
                        startDateTime = dateTime.minusMinutes(2),
                        endDateTime = dateTime.minusMinutes(1),
                        startLocation = location,
                        endLocation = location,
                        startLocationName = startLocationName,
                        endLocationName = endLocationName,
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
                    )
                    + stageUiStates.slice(1..stageUiStates.lastIndex )
            )
        )
    }

     fun addStageUiStateAfter(tripUiStateId: Long) {
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
                        startDateTime = dateTime.plusMinutes(1),
                        endDateTime = dateTime.plusMinutes(2),
                        startLocation = location,
                        endLocation = location,
                        startLocationName = startLocationName,
                        endLocationName = endLocationName,
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

    fun updateTripPurpose(tripUiState: TripUiState) {
        viewModelScope.launch {
            tripAndStageRepository.updateTripPurpose(
                tripId = tripUiState.tripId,
                purpose = tripUiState.purpose
            )
        }
    }

    private fun updateTrip(tripUiStateId: Long) {
        val tripUiState = getTripUiState(tripUiStateId)

        var errorHasOccurred = false
        var errorMessage = ""

        updateTripPurpose(tripUiState)
        try {
            updateStages(tripUiState)
        } catch (exception: Exception) {
            errorHasOccurred = true
            errorMessage = exception.message.toString()
        }

        if (errorHasOccurred) {
            throw IllegalArgumentException(errorMessage)
        }
    }

    private fun GeoPoint.toLocation(time: Long): Location {
        val location = Location("osmdroid")
        location.latitude = this.latitude
        location.longitude = this.longitude
        location.time = time
        return location
    }

     fun donateTrips(trips: List<TripUiState>) {
        viewModelScope.launch {
            val tripsToSend: MutableList<Long> = mutableListOf()
            for (trip in trips) {
                tripsToSend.add(trip.tripId)
            }
            tripAndStageRepository.saveTripsAndStagesToNetwork(tripsToSend)
        }
    }
}