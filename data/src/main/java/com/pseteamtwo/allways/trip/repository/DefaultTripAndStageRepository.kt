package com.pseteamtwo.allways.trip.repository

import android.location.Location
import com.pseteamtwo.allways.account.repository.AccountRepository
import com.pseteamtwo.allways.di.ApplicationScope
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
import com.pseteamtwo.allways.trip.source.local.LocalStageWithoutGpsPoints
import com.pseteamtwo.allways.trip.source.local.LocalTrip
import com.pseteamtwo.allways.trip.source.local.LocalTripWithoutStages
import com.pseteamtwo.allways.trip.source.local.StageDao
import com.pseteamtwo.allways.trip.source.local.TripDao
import com.pseteamtwo.allways.trip.source.network.StageNetworkDataSource
import com.pseteamtwo.allways.trip.source.network.TripNetworkDataSource
import com.pseteamtwo.allways.trip.toExternal
import com.pseteamtwo.allways.trip.toLocal
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
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

    internal fun observeAllGpsPoints(): StateFlow<List<LocalGpsPoint>> {
        return gpsPointLocalDataSource.observeAll()
    }

    override suspend fun observeAllTrips(): Flow<List<Trip>> {
        return tripLocalDataSource.observeAll().map { trip ->
            trip.toExternal().sortedBy { it.startDateTime }
        }
    }

    override suspend fun observeStagesOfTrip(tripId: Long): Flow<List<Stage>> {
        return tripLocalDataSource.observe(tripId).map { trip ->
            trip.stages.toExternal().sortedBy { stage ->  stage.startDateTime } 
        }
    }

    override suspend fun createTrip(stages: List<Stage>, purpose: Purpose) {
        require(stages.isNotEmpty())
        // asserting stages aren't empty, stages are in local db, stages aren't assigned to a trip
        for (stage in stages) {
            assert(stageLocalDataSource.get(stage.id)?.stageData?.tripId == 0L) {
                "Stage with ID ${stage.id} is either missing or already assigned to another trip"
            }
        }

        val localTripWithoutStages = LocalTripWithoutStages(
            purpose = purpose,
            isConfirmed = false
        )

        // inserts the local trip without stages to generate the trip id
        val tripId = tripLocalDataSource.insert(localTripWithoutStages)

        val localStages = stages.toLocal(tripId)
        localStages.forEach {
            stageLocalDataSource.update(it)
        }

        val localTrip = LocalTrip(
            tripData = localTripWithoutStages.copy(id = tripId),
            stages = localStages
        )
        tripLocalDataSource.update(localTrip)
    }

    // this seems to be for the tracking algorithm
    // creates trips that don't belong to any trip!
    internal suspend fun createStage(localGpsPoints: List<LocalGpsPoint>, mode: Mode): Stage {
        require(localGpsPoints.isNotEmpty())
        val localStageWithoutGpsPoints = LocalStageWithoutGpsPoints(
            mode = mode
        )

        // inserts the local stage without GPS points to generate the stage id
        val stageId = stageLocalDataSource.insert(localStageWithoutGpsPoints)
        if (stageId == -1L) {
            throw NoTimeContinuityException()
        }

        localGpsPoints.forEach { it.stageId = stageId }
        val localStage = LocalStage(
            stageData = localStageWithoutGpsPoints.copy(id = stageId),
            gpsPoints = localGpsPoints
        )

        stageLocalDataSource.update(localStage)
        return localStage.toExternal()
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

        localTrip.tripData.purpose = purpose
        tripLocalDataSource.update(localTrip)
    }

    override suspend fun updateStage(
        stageId: Long,
        mode: Mode,
        startTime: LocalDateTime,
        endTime: LocalDateTime,
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

        localStage.stageData.mode = mode

        // check for time conflicts with other stages
        if (isTimeConflict(startTime.toMillis(), endTime.toMillis(), stageId)) {
            throw NoTimeContinuityException()
        }


        // check if either of the locations has been changed
        val startLocationOfLocalStage = localStage.gpsPoints.first().location
        val endLocationOfLocalStage = localStage.gpsPoints.last().location
        val startTimeMillis = startTime.toMillis()
        val endTimeMillis = endTime.toMillis()

        if (startLocation.compareTo(startLocationOfLocalStage)
            && endLocation.compareTo(endLocationOfLocalStage)) {
            // start and end location haven't changed
            // set new start and end time
            localStage.gpsPoints.first().location.time = startTimeMillis
            localStage.gpsPoints.last().location.time = endTimeMillis
        } else {
            // start and end location have changed
            // create new GPS Points and update GPS Points List of Stage
            val startLocalGpsPoint = createGpsPoint(startLocation.toLocation(startTimeMillis)).toLocal(stageId)
            val endLocalGpsPoint = createGpsPoint(endLocation.toLocation(endTimeMillis)).toLocal(stageId)
            gpsPointLocalDataSource.update(startLocalGpsPoint)
            gpsPointLocalDataSource.update(endLocalGpsPoint)

            localStage.gpsPoints = listOf(startLocalGpsPoint, endLocalGpsPoint)
        }

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
        val localTrip = tripLocalDataSource.get(tripId)

        // does trip exist
        if (localTrip == null) {
            assert(false) { "Trip with ID $tripId not found in database" }
            return
        }

        val localStageWithoutGpsPoints = LocalStageWithoutGpsPoints(
            tripId = tripId,
            mode = mode
        )

        val stageId = stageLocalDataSource.insert(localStageWithoutGpsPoints)

        val localStage = LocalStage(
            stageData = localStageWithoutGpsPoints.copy(id = stageId),
            gpsPoints = listOf(
                LocalGpsPoint(
                    stageId = stageId,
                    location = startLocation
                ),
                localTrip.stages.first().gpsPoints.first()
            )
        )

        stageLocalDataSource.update(localStage)
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

        val localStageWithoutGpsPoints = LocalStageWithoutGpsPoints(
            tripId = tripId,
            mode = mode
        )

        val stageId = stageLocalDataSource.insert(localStageWithoutGpsPoints)

        val localStage = LocalStage(
            stageData = localStageWithoutGpsPoints.copy(id = stageId),
            gpsPoints = listOf(
                localTrip.stages.last().gpsPoints.last(),
                LocalGpsPoint(
                    stageId = stageId,
                    location = endLocation
                )
            )
        )

        stageLocalDataSource.update(localStage)
    }

    override suspend fun separateStageFromTrip(stageId: Long) {
        val localStage = stageLocalDataSource.get(stageId)

        // does stage exist
        if (localStage == null) {
            assert(false) { "Stage with ID $stageId not found in database" }
            return
        }

        // update trip
        val localTripOfStage = localStage.stageData.tripId?.let { tripLocalDataSource.get(it) }

        if (localTripOfStage == null) {
            assert(false) { "Stage does not belong to any trip" }
            return
        }

        val stagesOfTrip = localTripOfStage.stages.toMutableList()
        stagesOfTrip.find { it.stageData.id == stageId }?.let { stagesOfTrip.remove(it) }
        val updatedTrip = localTripOfStage.copy(stages = stagesOfTrip)
        tripLocalDataSource.update(updatedTrip)

        // remove tripId from stage
        val localStageWithoutTripId =
            localStage.copy(stageData = localStage.stageData.copy(tripId = null))

        // create trip and change stage
        val tripId = createTrip(
            listOf(localStageWithoutTripId.toExternal()),
            localTripOfStage.tripData.purpose
        )
    }

    override suspend fun deleteTrip(tripId: Long) {
        tripLocalDataSource.delete(tripId)
    }

    // TODO if it is the only stage in the trip, should it delete the trip or not delete the stage
    override suspend fun deleteStage(stageId: Long) {
        val localStage = stageLocalDataSource.get(stageId)

        if (localStage == null) {
            assert(false) { "Stage does not exist in database" }
            return
        }

        val localTripOfStage = localStage.stageData.tripId?.let { tripLocalDataSource.get(it) }

        if (localTripOfStage == null) {
            assert(false) { "Trip of stage not found" }
            return
        }

        if (localTripOfStage.stages.size == 1) {
            deleteTrip(localTripOfStage.tripData.id)
        } else {
            localTripOfStage.stages.filterNot { it.stageData.id == stageId }
            tripLocalDataSource.update(localTripOfStage)

            stageLocalDataSource.delete(stageId)
        }
    }

    // TODO can this be called with only one trip by ui? IllegalArgumentException
    override suspend fun connectTrips(tripIds: List<Long>) {
        val localTrips = mutableListOf<LocalTrip>()
        tripIds.forEach { tripId ->
            tripLocalDataSource.get(tripId)?.let { localTrips.add(it) }
        }

        require(localTrips.size >= 2) { "Needs at least two trip to connect" }

        // checks if the trips are subsequent
        val allTrips = withContext(dispatcher) {
            observeAllTrips().first()
        }
        if (isSubsequenceWithoutInterruptions(allTrips, localTrips.toList().toExternal())) {
            throw TimeTravelException()
        }

        val localStages = mutableListOf<LocalStage>()
        localTrips.forEach { localStages.addAll(it.stages) }

        // checks if the end and start locations between the trips match
        for (i in 1 until localStages.size - 1) {
            if (localStages[0].gpsPoints.last().location
                != localStages[1].gpsPoints.first().location) {
                throw TeleportationException()
            }
        }

        createTrip(localStages.toExternal(), Purpose.NONE)
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


    /*
    private fun createTripId(): String {
        TODO("Not yet implemented")
    }

    private fun createStageId(): String {
        TODO("Not yet implemented")
    }

    private fun createGpsPointId(): String {
        TODO("Not yet implemented")
    }
     */

    // TODO
    private fun GeoPoint.toLocation(time: Long): Location {
        val location = Location("osmdroid")
        location.latitude = this.latitude
        location.longitude = this.longitude
        location.time = time
        return location
    }

    private fun LocalDateTime.toMillis(): Long {
        return this.toInstant(ZoneOffset.UTC).toEpochMilli()
    }

    private suspend fun isTimeConflict(startTime: Long, endTime: Long, excludedStageId: Long): Boolean {
        return stageLocalDataSource.getAll().any { stage ->
            if (stage.stageData.id == excludedStageId) {
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