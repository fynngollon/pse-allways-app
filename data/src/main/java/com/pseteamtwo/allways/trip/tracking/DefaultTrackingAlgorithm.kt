package com.pseteamtwo.allways.trip.tracking

import android.location.Location
import com.pseteamtwo.allways.di.ApplicationScope
import com.pseteamtwo.allways.di.DefaultDispatcher
import com.pseteamtwo.allways.trip.Mode
import com.pseteamtwo.allways.trip.Purpose
import com.pseteamtwo.allways.trip.repository.DefaultTripAndStageRepository
import com.pseteamtwo.allways.trip.source.local.GpsPointDao
import com.pseteamtwo.allways.trip.source.local.LocalGpsPoint
import com.pseteamtwo.allways.trip.source.local.LocalStage
import com.pseteamtwo.allways.trip.source.local.LocalTrip
import com.pseteamtwo.allways.trip.source.local.LocalTripWithStages
import com.pseteamtwo.allways.trip.source.local.StageDao
import com.pseteamtwo.allways.trip.source.local.TripDao
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

class DefaultTrackingAlgorithm @Inject constructor(
    private val tripAndStageRepository: DefaultTripAndStageRepository,
    private val gpsPointDao: GpsPointDao,
    private val stageDao: StageDao,
    private val tripDao: TripDao,
    @DefaultDispatcher private val dispatcher: CoroutineDispatcher,
    @ApplicationScope private val scope: CoroutineScope
) : TrackingAlgorithm {

    /**
     * Manually (for now) retrieves the latest GPS points from the local database, that have been
     * tracked since the last trip. Then it tries to predict trips from the data. If the algorithm
     * predicts a trip, it subdivides the trip into stages and predicts a mode.
     * Then, the predicted trips and their stages are saved in the local database. Lastly,
     * GPS points that are between the trips (including the latest trip that already existed before)
     * are deleted from the database to save storage.
     */
    override fun observeTrackingData() {
        val gpsPointsInDatabase = mutableListOf<LocalGpsPoint>()
        val gpsPoints = mutableListOf<LocalGpsPoint>()
        var indexOfFirstTripMember = -1

        // Retrieve GPS points
        scope.launch(dispatcher) {
            gpsPointsInDatabase.addAll(observeAllGpsPoints().first())
            indexOfFirstTripMember = gpsPointsInDatabase.indexOfFirst { gpsPoint ->
                gpsPoint.stageId != null
            }
            if (indexOfFirstTripMember == -1) gpsPoints.addAll(gpsPointsInDatabase)
            else gpsPoints.addAll(gpsPointsInDatabase.subList(0, indexOfFirstTripMember))


            // Predict trips
            val trips = predictTrips(gpsPoints)

            // Delete GPS points that are no longer needed
            if (trips.isNotEmpty()) {
                val oldestGpsPointOfTrip =
                    tripDao.getTripWithStages(trips.first().id) as LocalTripWithStages

                val gpsPointsToDelete = gpsPoints.subList(
                    gpsPoints.indexOf(oldestGpsPointOfTrip.stages.first().gpsPoints.first()),
                    gpsPoints.size
                )

                gpsPointsToDelete.forEach {
                    gpsPointDao.delete(it.id)
                }
            }
        }
    }

    /**
     * Prerequisites: We have a fix location x0, and varying locations y
     * 1. Check if distance of x0 and y is greater than 50m [MIN_DISTANCE_FOR_TRIP_START] not anymore
     *    and avg speed greater than WHAT?
     * 2. Trip should maybe go on for a minimum time.
     * 2. Takes the WHICH GPS point as start.
     * 3. Trip ends when the user was still for more than 15 [MAX_MOTIONLESS_IN_TRIP] minutes
     */
    private suspend fun predictTrips(gpsPoints: List<LocalGpsPoint>): List<LocalTrip> {
        val potentialTrips = mutableListOf<MutableList<LocalGpsPoint>>()
        val trips = mutableListOf<LocalTrip>()

        // Creates potential trips
        for (i in gpsPoints.size - 1 downTo 0) {
            // Skip GPS points that are already in a potential trip
            if (potentialTrips.isNotEmpty() &&
                gpsPoints[i].location.time <= potentialTrips.last().last().location.time) {
                continue
            }

            if (gpsPoints[i].location.speed > STILL_MOTION_THRESHOLD) {
                // Start potential trip
                val potentialTrip = mutableListOf(gpsPoints[i])
                var lastMotionAsTimestamp = gpsPoints[i].location.time
                var j = -1

                while (i + j >= 0 &&
                    gpsPoints[i + j].location.time < lastMotionAsTimestamp + MAX_MOTIONLESS_IN_TRIP) {
                    potentialTrip.add(gpsPoints[i + j])

                    // Resets timer until trip is finished if motion was detected
                    if (gpsPoints[i + j].location.speed > STILL_MOTION_THRESHOLD) {
                        lastMotionAsTimestamp = gpsPoints[i + j].location.time
                    }

                    j--
                }

                potentialTrips.add(potentialTrip)
            }

            val unsavedTrips = mutableListOf<List<LocalGpsPoint>>()

            // Iterates over all potential trips, strips them of trailing motionless GPS points
            // and removes those whose duration or distance isn't long enough
            potentialTrips.forEach { potentialTrip ->
                val strippedPotentialTrip = potentialTrip.dropLastWhile {
                    it.location.speed == STILL_MOTION_THRESHOLD
                }

                // Checks if trip duration is long enough
                val tripDuration = strippedPotentialTrip.last().location.time -
                        strippedPotentialTrip.first().location.time
                if (tripDuration < MIN_DURATION_OF_TRIP) {
                    return@forEach
                }

                // Checks if trip distance is long enough
                val tripDistance = calculateDistance(strippedPotentialTrip.map { it.location })
                if (tripDistance < MIN_DISTANCE_OF_TRIP) {
                    return@forEach
                }

                unsavedTrips.add(strippedPotentialTrip.toMutableList())
            }

            // Predicts stages
            unsavedTrips.forEach { trip ->
                val stages = predictStages(trip)

                trips.add(tripAndStageRepository.createTripOfExistingStages(stages, Purpose.NONE))
            }
        }

        return trips
    }

    /**
     * A stage has to be at least 3 minutes long. A new stage happens when someone is inactive for
     * at least 5 minutes or the speed changes significantly.
     */
    private suspend fun predictStages(gpsPoints: List<LocalGpsPoint>): List<LocalStage> {
        val stages = mutableListOf<LocalStage>()
        val potentialStagesAfterGeofencing = mutableListOf<List<LocalGpsPoint>>()
        val unsavedStages = mutableListOf<List<LocalGpsPoint>>()

        potentialStagesAfterGeofencing.addAll(predictStagesByGeofencing(gpsPoints))

        potentialStagesAfterGeofencing.forEach {
            unsavedStages.addAll(predictStagesBySpeedChange(it))
        }

        unsavedStages.forEach {
            stages.add(tripAndStageRepository.createStageOfExistingGpsPoints(it, predictMode(it)))
        }

        return stages
    }

    private suspend fun predictStagesByGeofencing(gpsPoints: List<LocalGpsPoint>): List<List<LocalGpsPoint>> {
        val currentStage = mutableListOf<LocalGpsPoint>()
        val potentialStages = mutableListOf<List<LocalGpsPoint>>()

        for (i in gpsPoints.indices) {
            currentStage.add(gpsPoints[i])

            if (hasNotMovedOutOfRadius(
                    gpsPoints.subList(i, gpsPoints.size).map { it.location })
            ) {
                // This should be a stay
                val startGpsPointOfNewStage =
                    tripAndStageRepository.createGpsPoint(currentStage.last().location)
                potentialStages.add(currentStage)
                currentStage.clear()
                currentStage.add(startGpsPointOfNewStage)
            }
        }

        return potentialStages
    }

    private fun hasNotMovedOutOfRadius(locations: List<Location>): Boolean {
        for (i in locations.indices) {
            val currentLocation = locations[i]
            val currentLocationTimestamp = currentLocation.time
            val windowEndTime = currentLocationTimestamp + FIVE_MINUTES_IN_MILLIS

            for (j in i + 1 until locations.size) {
                val nextLocation = locations[j]

                if (nextLocation.time > windowEndTime) break

                if (currentLocation.distanceTo(nextLocation) > MIN_DISTANCE_TO_TRAVEL_IN_FIVE_MINUTES) {
                    return true
                }
            }
        }

        return false
    }


    private suspend fun predictStagesBySpeedChange(gpsPoints: List<LocalGpsPoint>): List<List<LocalGpsPoint>> {
        val stages = mutableListOf<List<LocalGpsPoint>>()
        val currentStage = mutableListOf<LocalGpsPoint>()
        val currentSegment = mutableListOf<LocalGpsPoint>()

        for (i in gpsPoints.indices) {
            val gpsPoint = gpsPoints[i]
            val location = gpsPoints[i].location
            val locationSpeed = location.speed
            val locationTime = location.time

            // Keep adding locations while the minimum duration of the stage isn't reached
            if (currentStage.first().location.time - locationTime < MIN_DURATION_OF_STAGE) {
                currentStage.add(gpsPoint)
                continue
            }

            // Keep building a segment while the minimum duration of a stage isn't reached
            currentSegment.add(gpsPoint)
            if (currentSegment.first().location.time - locationTime < MIN_DURATION_OF_STAGE) {
                continue
            }

            if (predictMode(currentStage) == predictMode(currentSegment)) {
                currentStage.add(currentSegment.first())
                currentSegment.removeFirst()
            } else {
                // Since the Mode predicted Mode is different, we have to new stage!
                val startGpsPointOfNewStage =
                    tripAndStageRepository.createGpsPoint(currentStage.last().location)
                stages.add(currentStage)
                currentStage.clear()
                currentStage.add(startGpsPointOfNewStage)
                currentStage.addAll(currentSegment)
                currentSegment.clear()
            }
        }

        currentStage.addAll(currentSegment)
        stages.add(currentStage)

        return stages
    }


    private fun predictMode(gpsPoints: List<LocalGpsPoint>): Mode {
        val locations = gpsPoints.map { it.location }
        val averageSpeed = getAverageSpeed(locations)
        val peekSpeed = getPeekSpeed(locations)

        return when {
            averageSpeed < MAX_AVG_SPEED_WALKING && peekSpeed < MAX_PEEK_SPEED_WALKING -> Mode.WALK
            averageSpeed < MAX_AVG_SPEED_BICYCLE && peekSpeed < MAX_PEEK_SPEED_BICYCLE -> Mode.BICYCLE
            else -> Mode.CAR_DRIVER
        }
    }

    /**
     * Calculates the average speed of a list of locations.
     *
     * @param locations a list of locations to calculate the average speed of
     * @return the average speed of the locations in m/s
     */
    private fun getAverageSpeed(locations: List<Location>): Float {
        var averageSpeed = 0f

        locations.forEach { location ->
            averageSpeed += location.speed
        }

        return averageSpeed / locations.size
    }

    private fun getPeekSpeed(locations: List<Location>): Float {
        val sortedLocations = locations.sortedByDescending { it.speed }
        val endIndex = (locations.size * PERCENTILE_FOR_PEEK_SPEED).toInt()
        val topSpeedLocations = sortedLocations.take(endIndex)

        return getAverageSpeed(topSpeedLocations)
    }



    /**
     * Retrieves all [LocalGpsPoint]s saved in the local gpsPoint database sorted by their timestamp,
     * with the newest being first.
     *
     * @return A flow of all [LocalGpsPoint]s saved in the local gpsPoint database in form of a list.
     */
    private fun observeAllGpsPoints(): Flow<List<LocalGpsPoint>> {
        return gpsPointDao.observeAll().map { gpsPoint ->
            gpsPoint.sortedByDescending { it.location.time }
        }
    }

    companion object {
        const val PERCENTILE_FOR_PEEK_SPEED = 0.1 // in percentage
        const val STILL_MOTION_THRESHOLD = 0.001f // in meter per second
        const val MAX_MOTIONLESS_IN_TRIP = 900000 // in milliseconds
        const val MIN_DURATION_OF_TRIP = 900000 // in milliseconds
        const val MIN_DISTANCE_OF_TRIP = 500f // in meters
        const val MIN_DISTANCE_TO_TRAVEL_IN_FIVE_MINUTES = 50 // in meters
        const val FIVE_MINUTES_IN_MILLIS = 300000 // in milliseconds

        const val MIN_DURATION_OF_STAGE = 180000 // 3 min in milliseconds

        /**
         * The maximum average speed for [Mode.WALK] in m/s.
         * Expects walking speed to be between 0 and 10.8 km/h.
         */
        const val MAX_AVG_SPEED_WALKING = 3

        /**
         * The maximum peen speed for [Mode.WALK] in m/s.
         * Expects walking speed to not exceed 14.4 km/h.
         */
        const val MAX_PEEK_SPEED_WALKING = 4

        /**
         * The maximum average speed for [Mode.BICYCLE], [Mode.RUNNING], [Mode.E_BIKE] in m/s.
         * Expects speed to be between [MAX_AVG_SPEED_WALKING] and 30.6 km/h.
         */
        const val MAX_AVG_SPEED_BICYCLE = 8.5

        /**
         * The maximum peen speed for [Mode.BICYCLE], [Mode.RUNNING], [Mode.E_BIKE] in m/s.
         * Expects speed to not exceed 36 km/h.
         */
        const val MAX_PEEK_SPEED_BICYCLE = 10

    }
}