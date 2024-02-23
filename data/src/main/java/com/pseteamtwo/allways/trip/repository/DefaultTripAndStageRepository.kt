package com.pseteamtwo.allways.trip.repository

import android.location.Location
import com.pseteamtwo.allways.account.repository.AccountRepository
import com.pseteamtwo.allways.di.DefaultDispatcher
import com.pseteamtwo.allways.exception.NoTimeContinuityException
import com.pseteamtwo.allways.exception.TeleportationException
import com.pseteamtwo.allways.exception.TimeTravelException
import com.pseteamtwo.allways.exception.UserEnteredInvalidFormatException
import com.pseteamtwo.allways.trip.GpsPoint
import com.pseteamtwo.allways.trip.Mode
import com.pseteamtwo.allways.trip.Purpose
import com.pseteamtwo.allways.trip.Stage
import com.pseteamtwo.allways.trip.Trip
import com.pseteamtwo.allways.trip.isTimeInFuture
import com.pseteamtwo.allways.trip.source.local.GpsPointDao
import com.pseteamtwo.allways.trip.source.local.LocalGpsPoint
import com.pseteamtwo.allways.trip.source.local.LocalStage
import com.pseteamtwo.allways.trip.source.local.LocalStageWithGpsPoints
import com.pseteamtwo.allways.trip.source.local.LocalTrip
import com.pseteamtwo.allways.trip.source.local.LocalTripWithStages
import com.pseteamtwo.allways.trip.source.local.StageDao
import com.pseteamtwo.allways.trip.source.local.TripDao
import com.pseteamtwo.allways.trip.source.network.StageNetworkDataSource
import com.pseteamtwo.allways.trip.source.network.TripNetworkDataSource
import com.pseteamtwo.allways.trip.toExternal
import com.pseteamtwo.allways.trip.toLocal
import com.pseteamtwo.allways.trip.convertToMillis
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
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
 * @property stageLocalDataSource A [StageDao] to access the local trip database.
 * @property stageNetworkDataSource A [StageNetworkDataSource] to access the network stage database.
 * @property gpsPointLocalDataSource A [TripDao] to access the local trip database.
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
    //@ApplicationScope private val scope: CoroutineScope,
) : TripAndStageRepository {

    /**
     * Retrieves all [GpsPoint]s saved in the local gpsPoint database.
     *
     * @return A flow of all [GpsPoint]s saved in the local gpsPoint database in form of a list.
     */
    internal fun observeAllGpsPoints(): Flow<List<LocalGpsPoint>> {
        return gpsPointLocalDataSource.observeAll()
    }

    override suspend fun observeAllTrips(): Flow<List<Trip>> {
        return tripLocalDataSource.observeAllTripsWithStages().map { trip ->
            trip.toExternal().sortedBy { it.startDateTime }
        }
    }

    //TODO("This could also use [StageDao.getStagesForTrip]")
    override suspend fun observeStagesOfTrip(tripId: Long): Flow<List<Stage>> {
        return tripLocalDataSource.observeTripWithStages(tripId).map { trip ->
            trip.stages.toExternal().sortedBy { it.startDateTime }
        }
    }

    override suspend fun createTrip(stages: List<Stage>, purpose: Purpose) {
        require(stages.isNotEmpty())

        //check if stages are correct:
        //ids = 0, modes != NONE, gpsPoints.size = 2, timeContinuity in gpsPoints of each stage
        stages.forEach { stage ->
            if(stage.id == 0L || stage.mode == Mode.NONE || stage.gpsPoints.size != 2) {
                throw UserEnteredInvalidFormatException("Provided stages are invalid.")
            }
            val timeOfFirstGpsPoint = stage.gpsPoints.first().time
            val timeOfSecondGpsPoint = stage.gpsPoints.last().time
            val locationOfFirstGpsPoint = stage.gpsPoints.first().geoPoint
            val locationOfSecondGpsPoint = stage.gpsPoints.last().geoPoint
            if(isTimeInFuture(timeOfFirstGpsPoint) || isTimeInFuture(timeOfSecondGpsPoint)) {
                throw UserEnteredInvalidFormatException("At least 1 stage contains gpsPoints" +
                        "with times in the future which is invalid.")
            }
            if(timeOfFirstGpsPoint.isAfter(timeOfSecondGpsPoint)) {
                throw UserEnteredInvalidFormatException("At least 1 stage contains gpsPoints" +
                        "with invalid time continuity.")
            }
            if(timeOfFirstGpsPoint.isEqual(timeOfSecondGpsPoint)) {
                throw UserEnteredInvalidFormatException("At least 1 stage has a duration of 0.")
            }
            if(locationOfFirstGpsPoint == locationOfSecondGpsPoint) {
                throw UserEnteredInvalidFormatException("At least 1 stage has a distance of 0.")
            }
        }
        //check for time and space continuity between stages of trip to create
        for(i in 0 until stages.size - 1) {
            val prevEndTime = stages[i].endDateTime
            val nextStartTime = stages[i+1].startDateTime
            if(prevEndTime.isAfter(nextStartTime)) {
                throw UserEnteredInvalidFormatException("No time continuity between stages.")
            }
            val prevEndLocation = stages[i].endLocation
            val nextStartLocation = stages[i+1].startLocation
            if(prevEndLocation != nextStartLocation) {
                throw UserEnteredInvalidFormatException("Not the same location between stages.")
            }
        }

        if(isTimeConflictInTrips(stages.first().startDateTime, stages.last().endDateTime)) {
            throw UserEnteredInvalidFormatException("Entered trip interferes with other trips" +
                    "already existent in the local database.")
        }
        //At this point, consistency checks should be done and the trip to create can be created

        val tripWithoutId = LocalTrip(purpose = purpose, isConfirmed = true)
        val createdTripId = tripLocalDataSource.insert(tripWithoutId)

        stages.forEach { stage ->
            val stageWithoutId = LocalStage(tripId = createdTripId, mode = stage.mode)
            val createdStageId = stageLocalDataSource.insert(stageWithoutId)

            stage.gpsPoints.forEach { gpsPoint ->
                val location = gpsPoint.geoPoint.toLocation(gpsPoint.time.convertToMillis())
                val gpsPointWithoutId = LocalGpsPoint(stageId = createdStageId, location = location)
                gpsPointLocalDataSource.insert(gpsPointWithoutId)
            }
        }
    }

    internal suspend fun createTripOfExistingStages(
        localStages: List<LocalStage>,
        purpose: Purpose
    ): LocalTrip {
        require(localStages.isNotEmpty())
        // stages are in local db and aren't assigned to a trip
        localStages.forEach {
            if(stageLocalDataSource.get(it.id) == null) {
                assert(false) { "A stage is missing in the database" }
            }
            assert(stageLocalDataSource.get(it.id)?.tripId == null) {
                "A stage is already assigned to another trip"
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
                assert(false) { "Locations between trips to connect have to be same" }
            }
            val prevEndTime = prevEndLocation.time
            val nextStartTime = nextStartLocation.time
            if(prevEndTime > nextStartTime) {
                assert(false) { "Times between trips do not allow time travel" }
            }
        }

        val localTripWithoutIds = LocalTrip(
            purpose = purpose,
            isConfirmed = false
        )

        // inserts the local trip without stages to generate the trip id
        val tripId = tripLocalDataSource.insert(localTripWithoutIds)

        localStages.forEach {
            stageLocalDataSource.update(it.copy(tripId = tripId))
        }

        return localTripWithoutIds.copy(id = tripId)
    }

    /**
     * Creates a new [LocalStage] with the provided parameters and converts it to a [Stage].
     * Therefore creates a unique id for the new stage and saves it into the local stage database.
     * The provided [LocalGpsPoint]s have to be in the database already with no stages assigned
     * to them.
     *
     * @param localGpsPoints The list of [LocalGpsPoint]s which the new stage consists of.
     * @param mode The [Mode] of the new stage.
     * @return The created [Stage].
     */
    // this seems to be for the tracking algorithm
    // creates trips that don't belong to any trip!
    internal suspend fun createStageOfExistingGpsPoints(
        localGpsPoints: List<LocalGpsPoint>,
        mode: Mode
    ): LocalStage {
        require(localGpsPoints.isNotEmpty())
        // gpsPoints are in local db and aren't assigned to a stage
        localGpsPoints.forEach {
            if(gpsPointLocalDataSource.get(it.id) == null) {
                assert(false) { "A gpsPoint is missing in the database" }
            }
            assert(gpsPointLocalDataSource.get(it.id)?.stageId == null) {
                "A gpsPoint is already assigned to another stage"
            }
            if(isTimeInFuture(it.location.time)) {
                assert(false) { "Time of gpsPoints to create a stage out of" +
                        " may not be in the future" }
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

        //val localStage = localStageWithoutUpdatedIds.copy(id = stageId)
        // alt: val localStage = localStageWithoutUpdatedIds.copy(id = stageId, gpsPoints = localGpsPoints)
        //TODO("this has to be deleted after ensuring right functionality")
        val createdStage = Stage(stageId, mode, localGpsPoints.toExternal())
        val createdStageOutOfDatabase = stageLocalDataSource.getStageWithGpsPoints(stageId)
        assert(createdStageOutOfDatabase != null) {
            "Created Stage could not be added to the database (or not in the right way)"
        }
        assertEquals(createdStage, createdStageOutOfDatabase?.toExternal())

        return localStageWithoutUpdatedId.copy(id = stageId)
    }

    /**
     * Creates a new [LocalGpsPoint] with the provided [Location].
     * Therefore creates a unique id for the new gpsPoint and saves it
     * into the local gpsPoint database.
     *
     * @param location The [Location] which the new gpsPoint consists of.
     * @return The created gpsPoint.
     */
    internal suspend fun createGpsPoint(location: Location): LocalGpsPoint {
        if(isTimeInFuture(location.time)) {
            assert(false) { "Time of gpsPoint to create may not be in the future" }
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
        }

        // check if trip is in db
        if (localTrip == null) {
            assert(false) { "Trip with ID $tripId not found in database" }
            return
        }

        localTrip.purpose = purpose
        tripLocalDataSource.update(localTrip)
    }

    override suspend fun updateStage(
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

        // check for time conflicts with other stages
        if (isTimeConflict(
                startDateTime.convertToMillis(),
                endDateTime.convertToMillis(),
                stageId
        )) {
            throw NoTimeContinuityException()
        }

        //TODO("This could also use [GpsPointDao.getGpsPointsForStage] (which is not yet implemented)")
        val gpsPointsOfLocalStage = withContext(dispatcher) {
            stageLocalDataSource.getStageWithGpsPoints(stageId)!!.sortedGpsPoints
            //TODO("Not null assertion (!!.) maybe has to be deleted")
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

    // time continuity
    override suspend fun addUserStageBeforeTripStart(
        tripId: Long,
        mode: Mode,
        startDateTime: LocalDateTime,
        endDateTime: LocalDateTime,
        startLocation: Location
    ) {
        //TODO("Maybe with dispatcher")
        val localTrip = tripLocalDataSource.get(tripId)

        // does trip exist
        if (localTrip == null) {
            assert(false) { "Trip with ID $tripId not found in database" }
            return
        }

        val startGpsPoint = LocalGpsPoint(
            location = startLocation
        )
        //TODO("Maybe with dispatcher")
        val endGpsPoint =
            stageLocalDataSource.getStageWithGpsPoints(tripId)!!.
            sortedGpsPoints.first().copy(id = 0L)

        val startGpsPointId = gpsPointLocalDataSource.insert(startGpsPoint)
        val endGpsPointId = gpsPointLocalDataSource.insert(endGpsPoint)

        val localStage = LocalStage(
            tripId = tripId,
            mode = mode
        )

        val stageId = stageLocalDataSource.insert(localStage)
        gpsPointLocalDataSource.update(startGpsPoint.copy(id = startGpsPointId, stageId = stageId))
        gpsPointLocalDataSource.update(endGpsPoint.copy(id = endGpsPointId, stageId = stageId))
    }

    // time continuity
    override suspend fun addUserStageAfterTripEnd(
        tripId: Long,
        mode: Mode,
        startDateTime: LocalDateTime,
        endDateTime: LocalDateTime,
        endLocation: Location
    ) {
        val localTrip = tripLocalDataSource.get(tripId)

        // does trip exist
        if (localTrip == null) {
            assert(false) { "Trip with ID $tripId not found in database" }
            return
        }

        //TODO("Maybe with dispatcher")
        val startGpsPoint =
            stageLocalDataSource.getStageWithGpsPoints(tripId)!!.
            sortedGpsPoints.first().copy(id = 0L)
        val endGpsPoint = LocalGpsPoint(
            location = endLocation
        )
        val startGpsPointId = gpsPointLocalDataSource.insert(startGpsPoint)
        val endGpsPointId = gpsPointLocalDataSource.insert(endGpsPoint)

        val localStage = LocalStage(
            tripId = tripId,
            mode = mode
        )

        val stageId = stageLocalDataSource.insert(localStage)
        gpsPointLocalDataSource.update(startGpsPoint.copy(id = startGpsPointId, stageId = stageId))
        gpsPointLocalDataSource.update(endGpsPoint.copy(id = endGpsPointId, stageId = stageId))
    }

    override suspend fun separateStageFromTrip(stageId: Long) {
        val localStage = stageLocalDataSource.get(stageId)
        val stageWithGpsPoints = stageLocalDataSource.getStageWithGpsPoints(stageId)

        // does stage exist
        if (localStage == null) {
            assert(false) { "Stage with ID $stageId not found in database" }
            return
        }
        //is stage assigned to a trip
        if (localStage.tripId == null) {
            assert(false) { "Stage does not belong to any trip" }
            return
        }

        // update trip
        val localTripOfStage = localStage.tripId?.let { tripLocalDataSource.get(it) }
        //is trip existent
        if (localTripOfStage == null) {
            assert(false) { "Stage has a trip with id ${localStage.tripId} assigned " +
                    "but this trip does not exist" }
            return
        }

        val stagesOfTrip =
            tripLocalDataSource.getTripWithStages(localTripOfStage.id)!!.sortedStages
        //TODO("Not null assertion (!!.) maybe has to be deleted")

        if (stagesOfTrip.first() != stageWithGpsPoints
            && stagesOfTrip.last() != stageWithGpsPoints) {
            throw TimeTravelException()
        }

        // remove tripId from stage
        stageLocalDataSource.update(localStage.copy(tripId = null))

        // create trip and change stage
        //TODO("Not null assertion (!!.) maybe has to be deleted")
        createTrip(
            listOf(stageLocalDataSource.getStageWithGpsPoints(localStage.id)!!.toExternal()),
            localTripOfStage.purpose
        )
    }

    override suspend fun deleteTrip(tripId: Long) {
        tripLocalDataSource.delete(tripId)
    }

    // TODO if it is the only stage in the trip, should it delete the trip or not delete the stage
    override suspend fun deleteStage(stageId: Long) {
        val localStage = stageLocalDataSource.get(stageId)
        val stageWithGpsPoints = stageLocalDataSource.getStageWithGpsPoints(stageId)

        // does stage exist
        if (localStage == null) {
            assert(false) { "Stage with ID $stageId not found in database" }
            return
        }
        //is stage assigned to a trip
        if (localStage.tripId == null) {
            assert(false) { "Stage does not belong to any trip" }
            return
        }

        val localTripOfStage = localStage.tripId?.let { tripLocalDataSource.get(it) }
        //is trip existent
        if (localTripOfStage == null) {
            assert(false) { "Stage has a trip with id ${localStage.tripId} assigned " +
                    "but this trip does not exist" }
            return
        }

        val stagesOfTrip =
            tripLocalDataSource.getTripWithStages(localTripOfStage.id)!!.sortedStages
        //TODO("Not null assertion (!!.) maybe has to be deleted")

        if (stagesOfTrip.size == 1) {
            //trip only consisted of that stage so delete the whole trip
            deleteTrip(localTripOfStage.id)
        } else if(stageWithGpsPoints != stagesOfTrip.first()
            && stageWithGpsPoints != stagesOfTrip.last()) {
                stageLocalDataSource.delete(stageId)
        } else {
            //stage is in between other stages inside the trip
            //TODO("solution for this case not yet implemented")
            assert(false) { "Cannot delete stage which is not the beginning or ending" +
                    "stage of the trip in this version of the app" }
        }
    }

    // TODO can this be called with only one trip by ui? IllegalArgumentException
    override suspend fun connectTrips(tripIds: List<Long>) {
        val localTripsWithStages = mutableListOf<LocalTripWithStages>()
        tripIds.forEach { tripId ->
            tripLocalDataSource.getTripWithStages(tripId)?.let { localTripsWithStages.add(it) }
        }

        require(localTripsWithStages.size >= 2) { "Needs at least two trips to connect" }

        // checks if the trips are subsequent
        val allTrips = withContext(dispatcher) {
            observeAllTrips().first()
        }
        if (isSubsequenceWithoutInterruptions(
                allTrips,
                localTripsWithStages.toList().toExternal())
            ) {
            throw TimeTravelException()
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

    private fun isSubsequenceWithoutInterruptions(allTrips: List<Trip>, connectedTrips: List<Trip>): Boolean {
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
            trip.startDateTime.isAfter(startTime) && trip.startDateTime.isBefore(endTime)
        }
    }

    /*
    override suspend fun connectTripsAndStages() {
        TODO("Not yet implemented")
    }
     */

    override suspend fun loadTripsAndStagesFromNetwork() {
        TODO("Not yet implemented")
    }

    override suspend fun saveTripsAndStagesToNetwork(tripIds: List<String>) {
        TODO("Not yet implemented")
    }



    private suspend fun isTimeConflict(
        startTime: Long,
        endTime: Long,
        excludedStageId: Long
    ): Boolean {
        return stageLocalDataSource.getAllStagesWithGpsPoints().any { stage ->
            if (stage.stage.id == excludedStageId) {
                return@any false
            }
            val stageStartTime = stage.sortedGpsPoints.first().location.time
            val stageEndTime = stage.sortedGpsPoints.last().location.time

            val startOverlap = (startTime in (stageStartTime + 1)..<stageEndTime)
            val endOverlap = (endTime in (stageStartTime + 1)..<stageEndTime)
            val fullyContained = (stageStartTime > startTime && stageEndTime < endTime)

            startOverlap || endOverlap || fullyContained
        }
    }

    private suspend fun isTimeConflictInTrips(
        startTime: LocalDateTime,
        endTime: LocalDateTime
    ): Boolean {
        return tripLocalDataSource.getAllTripsWithStages().any { trip ->
            val tripStartTime = trip.sortedStages.first().sortedGpsPoints.first().location.time
            val tripEndTime = trip.sortedStages.last().sortedGpsPoints.last().location.time

            val startOverlap = (startTime.convertToMillis()
                    in (tripStartTime + 1)..<tripEndTime)
            val endOverlap = (endTime.convertToMillis()
                    in (tripStartTime + 1)..<tripEndTime)
            val fullyContained = (tripStartTime > startTime.convertToMillis()
                    && tripEndTime < endTime.convertToMillis())

            startOverlap || endOverlap || fullyContained
        }
    }


    private fun GeoPoint.toLocation(time: Long): Location {
        val location = Location("osmdroid")
        location.latitude = this.latitude
        location.longitude = this.longitude
        location.time = time
        location.speed = 0f
        return location
    }

    private fun GeoPoint.compareTo(location: Location): Boolean {
        return latitude == location.latitude && longitude == location.longitude
    }

    private fun Location.compareTo(location: Location): Boolean {
        return latitude == location.latitude && longitude == location.longitude
    }
}