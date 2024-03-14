package com.pseteamtwo.allways.data.trip.repository

import android.location.Location
import com.pseteamtwo.allways.data.account.repository.AccountRepository
import com.pseteamtwo.allways.data.di.ApplicationScope
import com.pseteamtwo.allways.data.di.DefaultDispatcher
import com.pseteamtwo.allways.data.exception.NoTimeContinuityException
import com.pseteamtwo.allways.data.exception.TeleportationException
import com.pseteamtwo.allways.data.exception.TimeTravelException
import com.pseteamtwo.allways.data.trip.GpsPoint
import com.pseteamtwo.allways.data.trip.Mode
import com.pseteamtwo.allways.data.trip.Purpose
import com.pseteamtwo.allways.data.trip.Stage
import com.pseteamtwo.allways.data.trip.Trip
import com.pseteamtwo.allways.data.trip.convertToLocalDateTime
import com.pseteamtwo.allways.data.trip.convertToMillis
import com.pseteamtwo.allways.data.trip.isTimeInFuture
import com.pseteamtwo.allways.data.trip.source.local.GpsPointDao
import com.pseteamtwo.allways.data.trip.source.local.LocalGpsPoint
import com.pseteamtwo.allways.data.trip.source.local.LocalStage
import com.pseteamtwo.allways.data.trip.source.local.LocalStageWithGpsPoints
import com.pseteamtwo.allways.data.trip.source.local.LocalTrip
import com.pseteamtwo.allways.data.trip.source.local.LocalTripWithStages
import com.pseteamtwo.allways.data.trip.source.local.StageDao
import com.pseteamtwo.allways.data.trip.source.local.TripDao
import com.pseteamtwo.allways.data.trip.source.network.StageNetworkDataSource
import com.pseteamtwo.allways.data.trip.source.network.TripNetworkDataSource
import com.pseteamtwo.allways.data.trip.toExternal
import com.pseteamtwo.allways.data.trip.toLocation
import com.pseteamtwo.allways.data.trip.toNetwork
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.osmdroid.util.GeoPoint
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

// stages can exist in the database without belonging to any trip:
// they won't be observed then; once added to a trip they will be deleted alongside the trip

/**
 * This implementation of [TripAndStageRepository] holds all local and network data access objects
 * for [Trip]s, [Stage]s and [GpsPoint]s.
 * For enabling saving those to the network database, it also has access to the [AccountRepository],
 * because the tracking data has to be saved under the according user account
 * on the network database.
 * This class follows the singleton-pattern.
 *
 * @property tripLocalDataSource A [TripDao] to access the local trip database.
 * @property tripNetworkDataSource A [TripNetworkDataSource] to access the network trip database.
 * @property stageLocalDataSource A [StageDao] to access the local stage database.
 * @property stageNetworkDataSource A [StageNetworkDataSource] to access the network stage database.
 * @property gpsPointLocalDataSource A [GpsPointDao] to access the local gpsPoint database.
 * @property accountRepository A [AccountRepository] to access the user's account data for saving
 * and retrieving data from the network database.
 * @property dispatcher A dispatcher to allow asynchronous function calls because this class uses
 * complex computing and many accesses to databases which shall not block the program flow.
 * @constructor Creates an instance of this class.
 */
@Singleton
class DefaultTripAndStageRepository @Inject constructor(
    private val tripLocalDataSource: TripDao,
    private val tripNetworkDataSource: TripNetworkDataSource,
    private val stageLocalDataSource: StageDao,
    private val stageNetworkDataSource: StageNetworkDataSource,
    private val gpsPointLocalDataSource: GpsPointDao,
    private val accountRepository: AccountRepository,
    @DefaultDispatcher private val dispatcher: CoroutineDispatcher,
    @ApplicationScope private val scope: CoroutineScope,
) : TripAndStageRepository {

    override suspend fun observeAllTrips(): Flow<List<Trip>> {
        return tripLocalDataSource.observeAllTripsWithStages().map { trip ->
            trip.toExternal().sortedByDescending { it.startDateTime }
        }
    }



    override suspend fun observeStagesOfTrip(tripId: Long): Flow<List<Stage>> {
        return tripLocalDataSource.observeTripWithStages(tripId).map { trip ->
            trip.stages.toExternal().sortedBy { it.startDateTime }
        }
    }



    override suspend fun createTrip(stages: List<Stage>, purpose: Purpose) {
        require(stages.isNotEmpty())
        //check if purpose is correct:
        if(purpose == Purpose.NONE) {
            throw IllegalArgumentException("Provided purpose cannot be NONE.")
        }
        //check if stages are correct:
        //modes != NONE, gpsPoints.size == 2, timeContinuity in gpsPoints of each stage
        stages.forEach { stage ->
            if(stage.mode == Mode.NONE || stage.gpsPoints.size != 2) {
                throw IllegalArgumentException("Provided stages are invalid.")
            }
            val timeOfFirstGpsPoint = stage.gpsPoints.first().time
            val timeOfSecondGpsPoint = stage.gpsPoints.last().time
            val geoPointOfFirstGpsPoint = stage.gpsPoints.first().geoPoint
            val geoPointOfSecondGpsPoint = stage.gpsPoints.last().geoPoint
            if(isTimeInFuture(timeOfFirstGpsPoint) || isTimeInFuture(timeOfSecondGpsPoint)) {
                throw TimeTravelException("At least 1 stage contains gpsPoints" +
                        " with times in the future which is invalid.")
            }
            if(timeOfFirstGpsPoint.isAfter(timeOfSecondGpsPoint)) {
                throw NoTimeContinuityException("At least 1 stage contains gpsPoints" +
                        " with invalid time continuity.")
            }
            if(timeOfFirstGpsPoint.isEqual(timeOfSecondGpsPoint)) {
                throw IllegalArgumentException("At least 1 stage has a duration of 0.")
            }
            if(geoPointOfFirstGpsPoint == geoPointOfSecondGpsPoint) {
                throw IllegalArgumentException("At least 1 stage has a distance of 0.")
            }
        }
        //check for time and space continuity between stages of trip to create
        for(i in 0 until stages.size - 1) {
            val prevEndTime = stages[i].endDateTime
            val nextStartTime = stages[i+1].startDateTime
            if(prevEndTime.isAfter(nextStartTime)) {
                throw NoTimeContinuityException("No time continuity between stages.")
            }
            val prevEndLocation = stages[i].endLocation
            val nextStartLocation = stages[i+1].startLocation
            if(prevEndLocation != nextStartLocation) {
                throw TeleportationException("Not the same location between stages.")
            }
        }

        if(isTimeConflictInTrips(stages.first().startDateTime, stages.last().endDateTime)) {
            throw TimeTravelException("Entered trip interferes with other trips" +
                    " already existent in the local database.")
        }
        //At this point, consistency checks should be done and the trip to create can be created

        val listOfStages = mutableListOf<LocalStage>()
        stages.forEach { stage ->
            val listOfGpsPoints = mutableListOf<LocalGpsPoint>()
            stage.gpsPoints.forEach { gpsPoint ->
                val location = gpsPoint.geoPoint.toLocation(gpsPoint.time.convertToMillis())
                listOfGpsPoints.add(createGpsPoint(location))
            }
            listOfStages.add(createStageOfExistingGpsPoints(listOfGpsPoints, stage.mode))
        }
        createTripOfExistingStages(listOfStages, purpose, true)

        //TODO("the following part is deprecated but still saved because it could be better on certain errors")
        /*val tripWithoutId = LocalTrip(purpose = purpose, isConfirmed = true)
        val createdTripId = tripLocalDataSource.insert(tripWithoutId)

        stages.forEach { stage ->
            val stageWithoutId = LocalStage(tripId = createdTripId, mode = stage.mode)
            val createdStageId = stageLocalDataSource.insert(stageWithoutId)

            stage.gpsPoints.forEach { gpsPoint ->
                val location = gpsPoint.geoPoint.toLocation(gpsPoint.time.convertToMillis())
                val gpsPointWithoutId = LocalGpsPoint(stageId = createdStageId, location = location)
                gpsPointLocalDataSource.insert(gpsPointWithoutId)
            }
        }*/
    }



    override suspend fun createTripOfExistingStages(
        localStages: List<LocalStage>,
        purpose: Purpose,
        isCreatedByUser: Boolean
    ): LocalTrip {
        require(localStages.isNotEmpty())
        // stages are in local db and aren't assigned to a trip
        localStages.forEach {
            if(stageLocalDataSource.get(it.id) == null) {
                throw IllegalArgumentException("Any given stage is missing in the local database.")
            }
            if(stageLocalDataSource.get(it.id)?.tripId != null) {
                throw IllegalArgumentException("Any given stage is already assigned" +
                        " to another trip.")
            }
        }
        //stages are continuous in physical logic of time and space
        val sortedStages = mutableListOf<LocalStageWithGpsPoints>()
        localStages.forEach { localStage ->
            stageLocalDataSource.getStageWithGpsPoints(localStage.id)!!.let {
                sortedStages.add(it)
            }
        }

        sortedStages.sortBy { it.sortedGpsPoints.first().location.time }
        for(i in 0 until sortedStages.size - 1) {
            val prevEndLocation =
                sortedStages[i].sortedGpsPoints.last().location
            val nextStartLocation =
                sortedStages[i+1].sortedGpsPoints.first().location
            if(!prevEndLocation.compareTo(nextStartLocation)) {
                throw IllegalArgumentException("Locations between stages to connect have" +
                        " to be the same.")
            }
            val prevEndTime = prevEndLocation.time
            val nextStartTime = nextStartLocation.time
            if(prevEndTime > nextStartTime) {
                throw IllegalArgumentException ("Times between stages do not allow time travel.")
            }
        }

        val localTripWithoutIds = LocalTrip(
            purpose = purpose,
            isConfirmed = isCreatedByUser
        )

        // inserts the local trip without stages to generate the trip id
        val tripId = tripLocalDataSource.insert(localTripWithoutIds)

        localStages.forEach {
            stageLocalDataSource.update(it.copy(tripId = tripId))
        }

        return localTripWithoutIds.copy(id = tripId)
    }



    // this seems to be for the tracking algorithm
    // creates stages that don't belong to any trip!
    override suspend fun createStageOfExistingGpsPoints(
        localGpsPoints: List<LocalGpsPoint>,
        mode: Mode
    ): LocalStage {
        require(localGpsPoints.isNotEmpty())
        // gpsPoints are in local db and aren't assigned to a stage
        localGpsPoints.forEach {
            if(gpsPointLocalDataSource.get(it.id) == null) {
                assert(false) { "A gpsPoint is missing in the database." }
            }
            assert(gpsPointLocalDataSource.get(it.id)?.stageId == null) {
                "A gpsPoint is already assigned to another stage."
            }
            if(isTimeInFuture(it.location.time)) {
                assert(false) { "Time of gpsPoints to create a stage out of" +
                        " may not be in the future." }
            }
        }

        val localStageWithoutUpdatedId = LocalStage(
            mode = mode
        )

        // inserts the local stage
        val stageId = stageLocalDataSource.insert(localStageWithoutUpdatedId)

        // TODO check for no time continuity
        //if (stageId == -1L) {
        //    throw NoTimeContinuityException()
        //}

        localGpsPoints.forEach {
            it.stageId = stageId
            gpsPointLocalDataSource.update(it)
        }

        // val localStage = localStageWithoutUpdatedIds.copy(id = stageId)
        // alt: val localStage = localStageWithoutUpdatedIds.copy(id = stageId, gpsPoints = localGpsPoints)
        //TODO("this has to be deleted after ensuring right functionality")
        val createdStage = Stage(stageId, mode, localGpsPoints.toExternal())
        val createdStageOutOfDatabase = stageLocalDataSource.getStageWithGpsPoints(stageId)
        assert(createdStageOutOfDatabase != null) {
            "Created Stage could not be added to the database (or not in the right way)."
        }
        assertEquals(createdStage, createdStageOutOfDatabase?.toExternal())

        return localStageWithoutUpdatedId.copy(id = stageId)
    }



    override suspend fun createGpsPoint(location: Location): LocalGpsPoint {
        if(isTimeInFuture(location.time)) {
            assert(false) { "Time of gpsPoint to create may not be in the future." }
        }

        val localGpsPoint = LocalGpsPoint(
            location = location
        )

        val gpsPointId = gpsPointLocalDataSource.insert(localGpsPoint)
        return localGpsPoint.copy(id = gpsPointId)
    }



    override suspend fun updateTripPurpose(tripId: Long, purpose: Purpose) {
        val localTrip = withContext(dispatcher) {
            tripLocalDataSource.get(tripId)
        } ?: throw IllegalArgumentException("Trip with ID $tripId not in local database.")

        localTrip.purpose = purpose
        tripLocalDataSource.update(localTrip)

        //If stages of this trip are already valid and purpose is valid, update trip as confirmed
        if(purpose != Purpose.NONE){
            val tripToUpdate = tripLocalDataSource.getTripWithStages(tripId)
            if(tripToUpdate!!.stages.all { it.stage.mode != Mode.NONE }) {
                tripLocalDataSource.updateConfirmed(tripId, true)
            }
        } else {
            tripLocalDataSource.updateConfirmed(tripId, false)
        }
    }



    override suspend fun updateStagesOfTrip(
        tripId: Long,
        stageIds: List<Long>,
        modes: List<Mode>,
        startDateTimes: List<LocalDateTime>,
        endDateTimes: List<LocalDateTime>,
        startLocations: List<GeoPoint>,
        endLocations: List<GeoPoint>
    ) {
        val tripToUpdate = tripLocalDataSource.getTripWithStages(tripId)
        if(tripToUpdate == null) {
            assert(false) { "Trip with provided id does not exist in database" }
        }
        val sizeOfStages = tripToUpdate!!.stages.size
        if(sizeOfStages != stageIds.size || sizeOfStages != modes.size
            || sizeOfStages != startDateTimes.size || sizeOfStages != endDateTimes.size
            || sizeOfStages != startLocations.size ||sizeOfStages != endLocations.size) {
            assert(false) { "Provided function parameters are invalid" }
        }
        if(tripToUpdate.sortedStages.map { it.stage.id } != stageIds) {
            assert(false) { "Provided stageIds are invalid for the provided tripId" }
        }

        //ensure that entered user data does not interfere with physical logic of time and space
        //within the trip to be updated itself
        if(modes.any { it == Mode.NONE }) {
            throw IllegalArgumentException("Provided mode of any stage cannot be NONE.")
        }
        for(i in 0 until sizeOfStages) {
            if(isTimeInFuture(startDateTimes[i]) || isTimeInFuture(endDateTimes[i])) {
                throw TimeTravelException("At least 1 provided time in future" +
                        "which is invalid.")
            }
            if(startDateTimes[i].isAfter(endDateTimes[i])) {
                throw NoTimeContinuityException("At least 1 stage with invalid time continuity.")
            }
            if(startDateTimes[i].isEqual(endDateTimes[i])) {
                throw IllegalArgumentException("At least 1 stage has a duration of 0.")
            }
            if(startLocations[i] == endLocations[i]) {
                throw IllegalArgumentException("At least 1 stage has a distance of 0.")
            }
        }
        for(i in 0 until sizeOfStages - 1) {
            if(endDateTimes[i].isAfter(startDateTimes[i+1])) {
                throw NoTimeContinuityException("Stages do not follow time continuity.")
            }
            if(endLocations[i] == startLocations[i+1]) {
                throw TeleportationException("Stages have to be continuous" +
                        "in locations.")
            }
        }

        //ensure that entered user data does not interfere with physical logic of time
        //compared to all other trips in the local database
        if(isTimeConflictInTrips(startDateTimes.first(), endDateTimes.last(), tripId)) {
            throw TimeTravelException("Entered times are not valid regarding" +
                    "all other trips in the local database")
        }
        //At this point, consistency checks should be done and the trip can be updated

        for(i in 0 until sizeOfStages) {
            updateStage(
                stageIds[i],
                modes[i],
                startDateTimes[i],
                endDateTimes[i],
                startLocations[i],
                endLocations[i]
            )
        }
        //If purpose of trip is already valid, update its confirmed-state to true
        if(tripToUpdate.trip.purpose != Purpose.NONE) {
            tripLocalDataSource.updateConfirmed(tripId, true)
        }
    }



    private suspend fun updateStage(
        stageId: Long,
        mode: Mode,
        startDateTime: LocalDateTime,
        endDateTime: LocalDateTime,
        startLocation: GeoPoint,
        endLocation: GeoPoint
    ) {
        val localStage = withContext(dispatcher) {
            stageLocalDataSource.get(stageId)
        }

        if (localStage == null) {
            assert(false) { "Stage with ID $stageId not found in database" }
            return
        }

        localStage.mode = mode

        val gpsPointsOfLocalStage = withContext(dispatcher) {
            stageLocalDataSource.getStageWithGpsPoints(stageId)!!.sortedGpsPoints
        }

        // check if either of the locations has been changed
        val startLocationOfLocalStage = gpsPointsOfLocalStage.first().location
        val endLocationOfLocalStage = gpsPointsOfLocalStage.last().location
        val startTimeMillis = startDateTime.convertToMillis()
        val endTimeMillis = endDateTime.convertToMillis()

        if (!startLocation.compareTo(startLocationOfLocalStage)
            || !endLocation.compareTo(endLocationOfLocalStage)
            || startLocationOfLocalStage.time != startTimeMillis
            || endLocationOfLocalStage.time != endTimeMillis) {
            //start or end location or start or end time have changed
            //create new start and end gpsPoint (inserted into database and assigned to localStage)
            val newStartGpsPoint = createGpsPoint(startLocation.toLocation(startTimeMillis))
            gpsPointLocalDataSource.update(newStartGpsPoint.copy(stageId = stageId))
            val newEndGpsPoint = createGpsPoint(endLocation.toLocation(endTimeMillis))
            gpsPointLocalDataSource.update(newEndGpsPoint.copy(stageId = stageId))

            //delete all former GpsPoints of the stage
            gpsPointsOfLocalStage.forEach { localGpsPoint ->
                gpsPointLocalDataSource.delete(localGpsPoint.id)
            }
            //create new start and end gpsPoint (inserted into database and assigned to localStage)
            createGpsPoint(startLocation.toLocation(startTimeMillis))
            createGpsPoint(endLocation.toLocation(endTimeMillis))
        }
        //update mode of localStage in database
        stageLocalDataSource.update(localStage)
    }



    override suspend fun addUserStageBeforeTripStart(
        tripId: Long,
        mode: Mode,
        startDateTime: LocalDateTime,
        endDateTime: LocalDateTime,
        startLocation: Location
    ) {
        if(isTimeInFuture(startDateTime) || isTimeInFuture(endDateTime)) {
            throw TimeTravelException("At least 1 provided time in future" +
                    "which is invalid.")
        }
        if(startDateTime.isAfter(endDateTime)) {
            throw NoTimeContinuityException("The stage has invalid time continuity.")
        }
        if(startDateTime.isEqual(endDateTime)) {
            throw IllegalArgumentException("The stage has a duration of 0.")
        }

        val tripToAddStageTo = tripLocalDataSource.getTripWithStages(tripId)
        // does trip exist
        if (tripToAddStageTo == null) {
            assert(false) { "Trip with ID $tripId not found in database" }
            return
        }
        val formerStartLocation =
            tripToAddStageTo.sortedStages.first().sortedGpsPoints.first().location

        if(startLocation == formerStartLocation) {
            throw IllegalArgumentException("The stage has a distance of 0.")
        }
        if(endDateTime.isAfter(formerStartLocation.time.convertToLocalDateTime())) {
            throw NoTimeContinuityException("No time continuity between stages.")
        }
        val tripEndDateTime = tripToAddStageTo.sortedStages.last().sortedGpsPoints.last().
            location.time.convertToLocalDateTime()
        if(isTimeConflictInTrips(startDateTime, tripEndDateTime, tripId)) {
            throw TimeTravelException("Entered stage interferes with other trips" +
                    "already existent in the local database.")
        }

        val startGpsPoint = createGpsPoint(startLocation)
        val endGpsPoint = createGpsPoint(formerStartLocation)
        val newStartStage = createStageOfExistingGpsPoints(listOf(startGpsPoint, endGpsPoint), mode)
        stageLocalDataSource.update(newStartStage.copy(tripId = tripId))
    }



    override suspend fun addUserStageAfterTripEnd(
        tripId: Long,
        mode: Mode,
        startDateTime: LocalDateTime,
        endDateTime: LocalDateTime,
        endLocation: Location
    ) {
        if(isTimeInFuture(startDateTime) || isTimeInFuture(endDateTime)) {
            throw TimeTravelException("At least 1 provided time in future" +
                    "which is invalid.")
        }
        if(startDateTime.isAfter(endDateTime)) {
            throw NoTimeContinuityException("The stage has invalid time continuity.")
        }
        if(startDateTime.isEqual(endDateTime)) {
            throw IllegalArgumentException("The stage has a duration of 0.")
        }

        val tripToAddStageTo = tripLocalDataSource.getTripWithStages(tripId)
        // does trip exist
        if (tripToAddStageTo == null) {
            assert(false) { "Trip with ID $tripId not found in database" }
            return
        }
        val formerEndLocation =
            tripToAddStageTo.sortedStages.last().sortedGpsPoints.last().location

        if(endLocation == formerEndLocation) {
            throw IllegalArgumentException("The stage has a distance of 0.")
        }
        if(startDateTime.isBefore(formerEndLocation.time.convertToLocalDateTime())) {
            throw NoTimeContinuityException("No time continuity between stages.")
        }
        val tripStartDateTime = tripToAddStageTo.sortedStages.first().sortedGpsPoints.first().
            location.time.convertToLocalDateTime()
        if(isTimeConflictInTrips(tripStartDateTime, endDateTime, tripId)) {
            throw TimeTravelException("Entered stage interferes with other trips" +
                    "already existent in the local database.")
        }

        val startGpsPoint = createGpsPoint(formerEndLocation)
        val endGpsPoint = createGpsPoint(endLocation)
        val newStartStage = createStageOfExistingGpsPoints(listOf(startGpsPoint, endGpsPoint), mode)
        stageLocalDataSource.update(newStartStage.copy(tripId = tripId))
    }



    override suspend fun separateStageFromTrip(stageId: Long) {
        val localStage = stageLocalDataSource.get(stageId)
            ?: throw IllegalArgumentException("Stage with ID $stageId not in local database.")

        //is stage assigned to a trip
        if (localStage.tripId == null) {
            throw IllegalArgumentException("Stage does not belong to any trip.")
        }

        val tripOfStage = tripLocalDataSource.getTripWithStages(localStage.tripId!!)
            ?: throw IllegalArgumentException("Stage has a trip with id ${localStage.tripId}" +
                    " assigned but this trip does not exist.")
        val stagesOfTrip = tripOfStage.sortedStages

        if (localStage != stagesOfTrip.first().stage && localStage != stagesOfTrip.last().stage) {
            throw IllegalArgumentException("Cannot separate stage which is not the beginning or" +
                    " ending stage of the trip in this version of the app.")
        }

        // remove tripId from stage
        stageLocalDataSource.update(localStage.copy(tripId = null))

        // create trip and change stage
        createTripOfExistingStages(
            listOf(localStage.copy(tripId = null)),
            tripOfStage.trip.purpose,
            true)
    }



    override suspend fun deleteTrip(tripId: Long) {
        tripLocalDataSource.delete(tripId)
    }



    override suspend fun deleteStage(stageId: Long) {
        val localStage = stageLocalDataSource.get(stageId)
            ?: throw IllegalArgumentException("Stage with ID $stageId not in local database.")

        //stage should be assigned to a trip because this method is to be called from the ui-layer
        if (localStage.tripId == null) {
            throw IllegalArgumentException("Stage does not belong to any trip.")
        }

        val tripOfStage = tripLocalDataSource.getTripWithStages(localStage.tripId!!)
            ?: throw IllegalArgumentException("Stage has a trip with id ${localStage.tripId}" +
                    " assigned but this trip does not exist.")
        val stagesOfTrip = tripOfStage.sortedStages

        if (stagesOfTrip.size == 1) {
            //trip only consisted of that stage so delete the whole trip
            deleteTrip(tripOfStage.trip.id)
        } else if(localStage == stagesOfTrip.first().stage
            || localStage == stagesOfTrip.last().stage) {
                stageLocalDataSource.delete(stageId)
        } else {
            //stage is in between other stages inside the trip which should not occur
            throw IllegalArgumentException("Cannot delete stage which is not the beginning or" +
                    " ending stage of the trip in this version of the app.")
        }
    }



    override suspend fun connectTrips(tripIds: List<Long>) {
        val localTripsWithStages = mutableListOf<LocalTripWithStages>()
        tripIds.forEach { tripId ->
            tripLocalDataSource.getTripWithStages(tripId)?.let { localTripsWithStages.add(it) }
        }

        if(localTripsWithStages.size <= 1) {
            throw IllegalArgumentException("Needs at least two trips to connect.")
        }

        // checks if the trips are subsequent
        val allTrips = withContext(dispatcher) {
            observeAllTrips().first()
        }
        if (!isSubsequentWithoutInterruptions(
                allTrips,
                localTripsWithStages.toList().toExternal()
            )) {
            throw TimeTravelException("There is a trip not to be connected between the trips to" +
                    "be connected.")
        }

        val localStagesWithGpsPoints = mutableListOf<LocalStageWithGpsPoints>()
        localTripsWithStages.forEach { localStagesWithGpsPoints.addAll(it.stages) }

        localTripsWithStages.sortBy {
            it.sortedStages.first().sortedGpsPoints.first().location.time
        }

        //checks if the start and end locations between the trips match
        //checks if the start and end time between trips don't interfere with physical logic of time
        for(i in 0 until localTripsWithStages.size - 1) {
            val prevEndLocation =
                localTripsWithStages[i].sortedStages.last().sortedGpsPoints.last().location
            val nextStartLocation =
                localTripsWithStages[i+1].sortedStages.first().sortedGpsPoints.first().location
            if(!prevEndLocation.compareTo(nextStartLocation)) {
                throw TeleportationException("Locations between trips to connect have to be same")
            }

            val prevEndTime = prevEndLocation.time
            val nextStartTime = nextStartLocation.time
            if(prevEndTime > nextStartTime) {
                throw TimeTravelException("Times between trips do not allow time travel")
            }
        }
        localStagesWithGpsPoints.forEach { localStageWithGpsPoints ->
            stageLocalDataSource.update(localStageWithGpsPoints.stage.copy(tripId = null))
        }

        //create new connected trip
        createTrip(localStagesWithGpsPoints.toExternal(), Purpose.NONE)
        tripIds.forEach { tripLocalDataSource.delete(it) }
    }



    override suspend fun getTripsOfDate(date: LocalDate): List<Trip> {
        return getTripsOfTimespan(date.atStartOfDay(), date.plusDays(1).atStartOfDay())
    }



    override suspend fun getTripsOfTimespan(
        startTime: LocalDateTime,
        endTime: LocalDateTime
    ): List<Trip> {
        val allTrips = withContext(dispatcher) {
            observeAllTrips().first()
        }

        return allTrips.filter { trip ->
            (trip.startDateTime.isAfter(startTime) || trip.startDateTime.isEqual(startTime))
                    && trip.startDateTime.isBefore(endTime)
        }
    }



    override suspend fun saveTripsAndStagesToNetwork(tripIds: List<Long>) {
        val tripsToUpload = mutableListOf<LocalTripWithStages>()

        tripIds.forEach {
            val localTripWithStagesNullable = withContext(dispatcher) {
                tripLocalDataSource.getTripWithStages(it)
            }

            assert(localTripWithStagesNullable != null) {
                "Trip ID $it does not exist in local database"
            }

            val localTripWithStages = localTripWithStagesNullable as LocalTripWithStages
            tripsToUpload.add(localTripWithStages)

            assert(localTripWithStages.trip.isConfirmed) {
                "Trip with ${localTripWithStages.trip.id} is not confirmed"
            }
        }

        val pseudonym = withContext(dispatcher) {
            accountRepository.observe().first().pseudonym
        }

        val networkTrips = tripsToUpload.toNetwork()

        scope.launch(dispatcher){
            tripNetworkDataSource.saveTrips(pseudonym, networkTrips)

            tripsToUpload.forEach {
                val networkStages = it.stages.toNetwork()

                stageNetworkDataSource.saveStages(pseudonym, networkStages)
            }
        }



    }



    /**
     * Checks if the provided times (representing start and end time of a trip) interfere
     * with existing trips in the database.
     *
     * @param startTime Start time to check.
     * @param endTime End time to check.
     * @param excludedTripId Trip to not check for interference due to it being the trip the
     * times should be checked of. If null, no trip is excluded in the check.
     * @return True, if there is such time conflict; false if no conflicts occur.
     */
    private suspend fun isTimeConflictInTrips(
        startTime: LocalDateTime,
        endTime: LocalDateTime,
        excludedTripId: Long? = null
    ): Boolean {
        return tripLocalDataSource.getAllTripsWithStages().any { trip ->
            if(excludedTripId != null) {
                if (trip.trip.id == excludedTripId) {
                    return@any false
                }
            }
            val tripStartTime = trip.sortedStages.first().sortedGpsPoints.first().location.time
            val tripEndTime = trip.sortedStages.last().sortedGpsPoints.last().location.time

            val startOverlap = (startTime.convertToMillis()
                    in (tripStartTime + 1)..<tripEndTime)
            val endOverlap = (endTime.convertToMillis()
                    in (tripStartTime + 1)..<tripEndTime)
            val fullyContained = (tripStartTime >= startTime.convertToMillis()
                    && tripEndTime <= endTime.convertToMillis())

            startOverlap || endOverlap || fullyContained
        }
    }

    /**
     * Checks if between the connected trips, there is any other trip not to be connected.
     *
     * @param allTrips All trips to be compared to.
     * @param connectedTrips Trips to be connected, which should be checked if there is a trip
     * between any of those which is not part of [connectedTrips].
     * @return True, if there is no problem with such trips in between; else false.
     */
    private fun isSubsequentWithoutInterruptions(
        allTrips: List<Trip>,
        connectedTrips: List<Trip>
    ): Boolean {
        var allTripsIndex = 0
        var connectedTripsIndex = 0

        while (allTripsIndex < allTrips.size && connectedTripsIndex < connectedTrips.size) {
            if (allTrips[allTripsIndex].id == connectedTrips[connectedTripsIndex].id) {
                connectedTripsIndex++
                allTripsIndex++
            } else {
                allTripsIndex++
            }
        }

        return connectedTripsIndex == connectedTrips.size
    }


    private fun GeoPoint.compareTo(location: Location): Boolean {
        return latitude == location.latitude && longitude == location.longitude
    }

    private fun Location.compareTo(location: Location): Boolean {
        return latitude == location.latitude && longitude == location.longitude
    }
}