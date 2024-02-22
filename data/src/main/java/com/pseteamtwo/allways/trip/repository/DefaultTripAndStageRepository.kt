package com.pseteamtwo.allways.trip.repository

import android.location.Location
import com.pseteamtwo.allways.account.repository.AccountRepository
import com.pseteamtwo.allways.di.DefaultDispatcher
import com.pseteamtwo.allways.exception.NoTimeContinuityException
import com.pseteamtwo.allways.exception.TeleportationException
import com.pseteamtwo.allways.exception.TimeTravelException
import com.pseteamtwo.allways.trip.GpsPoint
import com.pseteamtwo.allways.trip.Mode
import com.pseteamtwo.allways.trip.Purpose
import com.pseteamtwo.allways.trip.Stage
import com.pseteamtwo.allways.trip.Trip
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
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import org.osmdroid.util.GeoPoint
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneOffset
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
            trip.stages.toExternal().sortedBy { stage ->  stage.startDateTime }
        }
    }

    override suspend fun createTrip(stages: List<Stage>, purpose: Purpose) {
        require(stages.isNotEmpty())
        // stages are in local db and aren't assigned to a trip
        stages.forEach {
            if(stageLocalDataSource.get(it.id) == null) {
                assert(false) { "A stage is missing in the database" }
            }
            assert(stageLocalDataSource.get(it.id)?.tripId == null) {
                "A stage is already assigned to another trip"
            }
        }

        val localTripWithoutIds = LocalTrip(
            purpose = purpose,
            isConfirmed = false
        )

        // inserts the local trip without stages to generate the trip id
        val tripId = tripLocalDataSource.insert(localTripWithoutIds)

        val localStages = stages.toLocal(tripId)
        localStages.forEach {
            stageLocalDataSource.update(it)
        }
    }

    /**
     * Creates a new [LocalStage] with the provided parameters and converts it to a [Stage].
     * Therefore creates a unique id for the new stage and saves it into the local stage database.
     *
     * @param localGpsPoints The list of [LocalGpsPoint]s which the new stage consists of.
     * @param mode The [Mode] of the new stage.
     * @return The created [Stage].
     */
    // this seems to be for the tracking algorithm
    // creates trips that don't belong to any trip!
    internal suspend fun createStage(localGpsPoints: List<LocalGpsPoint>, mode: Mode): Stage {
        require(localGpsPoints.isNotEmpty())
        // gpsPoints are in local db and aren't assigned to a stage
        localGpsPoints.forEach {
            assert(gpsPointLocalDataSource.get(it.id)?.stageId == 0L) {
                "A GpsPoint is either missing in the database or already assigned to another stage"
            }
        }
        val localStageWithoutUpdatedIds = LocalStage(
            mode = mode
        )

        // inserts the local stage
        val stageId = stageLocalDataSource.insert(localStageWithoutUpdatedIds)

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

        val createdStage = Stage(stageId, mode, localGpsPoints.toExternal())
        val createdStageOutOfDatabase = stageLocalDataSource.getStageWithGpsPoints(stageId)
        assert(createdStageOutOfDatabase != null) {
            "Created Stage could not be added to the database (or not in the right way)"
        }
        assertEquals(createdStage, createdStageOutOfDatabase?.toExternal())
        return createdStage
    }

    // TODO should this be internal and LocalGpsPoint?
    // this seems to be for the tracking algorithm and maybe the ui
    // creates GPS points that don't belong to any stage!
    override suspend fun createGpsPoint(location: Location): GpsPoint {
        val localGpsPoint = LocalGpsPoint(
            location = location
        )

        val gpsPointId = gpsPointLocalDataSource.insert(localGpsPoint)
        return localGpsPoint.copy(id = gpsPointId).toExternal()
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
        if (isTimeConflict(startDateTime.toMillis(), endDateTime.toMillis(), stageId)) {
            throw NoTimeContinuityException()
        }

        //TODO("This could also use [GpsPointDao.getGpsPointsForStage] (which is not yet implemented)")
        val gpsPointsOfLocalStage = withContext(dispatcher) {
            stageLocalDataSource.getStageWithGpsPoints(stageId)!!.gpsPoints
            //TODO("Not null assertion (!!.) maybe has to be deleted")
        }

        // check if either of the locations has been changed
        val startLocationOfLocalStage = gpsPointsOfLocalStage.first().location
        val endLocationOfLocalStage = gpsPointsOfLocalStage.last().location
        val startTimeMillis = startDateTime.toMillis()
        val endTimeMillis = endDateTime.toMillis()

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
            createGpsPoint(startLocation.toLocation(startTimeMillis)).toLocal(stageId)
            createGpsPoint(endLocation.toLocation(endTimeMillis)).toLocal(stageId)
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
            stageLocalDataSource.getStageWithGpsPoints(tripId)!!.gpsPoints.first().copy(id = 0L)

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
            stageLocalDataSource.getStageWithGpsPoints(tripId)!!.gpsPoints.first().copy(id = 0L)
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
            stageLocalDataSource.getStagesForTrip(localTripOfStage.id).first().toMutableList()

        if (stagesOfTrip.first() != localStage && stagesOfTrip.last() != localStage) {
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
            stageLocalDataSource.getStagesForTrip(localTripOfStage.id).first().toMutableList()

        if (stagesOfTrip.size == 1) {
            //trip only consisted of that stage so delete the whole trip
            deleteTrip(localTripOfStage.id)
        } else if(localStage != stagesOfTrip.first() && localStage != stagesOfTrip.last()) {
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

        localTripsWithStages.sortBy { it.stages.first().gpsPoints.first().location.time }

        //checks if the start and end locations between the trips match
        //checks if the start and end time between trips don't interfere with physical logic of time
        for(i in 0 until localTripsWithStages.size - 1) {
            val prevEndLocation =
                localTripsWithStages[i].stages.last().gpsPoints.last().location
            val nextStartLocation =
                localTripsWithStages[i+1].stages.first().gpsPoints.first().location
            if(prevEndLocation == nextStartLocation) {//TODO("not sure if comparable like that")
                throw TeleportationException("Locations between trips to connect have to be same")
            }

            val prevEndTime =
                localTripsWithStages[i].stages.last().gpsPoints.last().location.time
            val nextStartTime =
                localTripsWithStages[i+1].stages.first().gpsPoints.first().location.time
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
        val startTimeInLong = startTime.toMillis()
        val endTimeInLong = endTime.toMillis()

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


    private fun GeoPoint.toLocation(time: Long): Location {
        val location = Location("osmdroid")
        location.latitude = this.latitude
        location.longitude = this.longitude
        location.time = time
        location.speed = 0f
        return location
    }

    private fun LocalDateTime.toMillis(): Long {
        return this.toInstant(ZoneOffset.UTC).toEpochMilli()
    }

    private suspend fun isTimeConflict(startTime: Long, endTime: Long, excludedStageId: Long): Boolean {
        return stageLocalDataSource.getAllStagesWithGpsPoints().any { stage ->
            if (stage.stage.id == excludedStageId) {
                return@any false
            }

            val startOverlap = (stage.gpsPoints.first().location.time < startTime
                    && stage.gpsPoints.last().location.time > startTime)
            val endOverlap = (stage.gpsPoints.first().location.time < endTime
                    && stage.gpsPoints.last().location.time > endTime)
            val fullyContained = (stage.gpsPoints.first().location.time > startTime
                    && stage.gpsPoints.last().location.time < endTime)

            startOverlap || endOverlap || fullyContained
        }
    }

    private fun GeoPoint.compareTo(location: Location): Boolean {
        return latitude == location.latitude && longitude == location.longitude
    }

}