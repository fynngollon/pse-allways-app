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
import com.pseteamtwo.allways.trip.source.local.TripDao
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * The current algorithm for predicting [LocalTrip]s, [LocalStage]s and their [Mode]s.
 *
 * Dev note:
 * This implementation was a backup plan solution since the activity recognition could not be
 * fully integrated yet. Originally, with activity recognition the prediction could have been
 * implemented more accurately. Hopefully, this class here can be replaced in the future.
 *
 * For now, the [observeTrackingData] has to be called manually to start the prediction. The
 * prediction takes all the GPS points that the tracking service has saved into the local database
 * since the last trip and predicts new trips.
 */
class DefaultTrackingAlgorithm @Inject constructor(
    private val tripAndStageRepository: DefaultTripAndStageRepository,
    private val gpsPointDao: GpsPointDao,
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
        var indexOfFirstTripMember: Int

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
     * Receives a list of [LocalGpsPoint]s and predicts trips based on the speed and time.
     * A potential trip starts when a tracked location speed exceeds the [STILL_MOTION_THRESHOLD]
     * threshold, the trip continues for at least [MIN_DURATION_OF_TRIP] minutes, has a distance
     * of at least [MIN_DISTANCE_OF_TRIP] meters and lastly has movement out of a radius of
     * [DIAMETER_OF_GEOFENCE]. These criteria ensure that the trip is long enough
     * and wasn't just within a building. Lastly, this function creates the trips it predicts
     * in the local database with [Purpose.NONE]. Internally this function calls the [predictStages]
     * function to predict the stages of the trip.
     *
     * @param gpsPoints a list of [LocalGpsPoint]s of which it predicts the trips.
     * @return the list of predicted [LocalTrip]s.
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

                // Checks if the trip ever went ouf a radius
                if (hasNotMovedOutOfRadius(strippedPotentialTrip.map { it.location },
                        tripDuration)) {
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
     * Predicts stages based on a list of [LocalGpsPoint]s. The provided list should already be a
     * legit trip, as the minimum criteria for a trip aren't checked. The prediction is divided
     * into two parts: prediction by geo-fencing, prediction by speed change. 
     * Firstly, the algorithm searches for periods where the movement keeps being in a radius,
     * defined by [DIAMETER_OF_GEOFENCE], for at least 5 minutes. This is considered a stay in
     * between stages and thus separates two stages.
     * Secondly, it looks out for speed changes and therefore [Mode] changes in the GPS points.
     * Since a stage always has a single [Mode], a [Mode] change also defines a new stage.
     * A stage has to be at least [MIN_DURATION_OF_STAGE] minutes long. During both separation
     * processes this criterion is always checked.
     * After the trip is separated into stages, the stages are saved into the local database.
     *
     * @param gpsPoints list of [LocalGpsPoint]s which represents a trip already.
     * @return a list of [LocalStage]s that define the stages the trip was separated to.
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

    /**
     * Searches for periods where the movement keeps being in a radius, defined by
     * [DIAMETER_OF_GEOFENCE], for at least 5 minutes. This is considered a stay in between stages
     * and thus separates two stages.
     *
     * @param gpsPoints list of [LocalGpsPoint]s which represents a trip already.
     * @return a list of [LocalGpsPoint]s that define potential stages to separate the trip into.
     */
    private suspend fun predictStagesByGeofencing(gpsPoints: List<LocalGpsPoint>): List<List<LocalGpsPoint>> {
        val currentStage = mutableListOf<LocalGpsPoint>()
        val potentialStages = mutableListOf<List<LocalGpsPoint>>()

        for (i in gpsPoints.indices) {
            currentStage.add(gpsPoints[i])

            val durationOfStage = currentStage.last().location.time - currentStage.first().location.time
            if (durationOfStage > MIN_DURATION_OF_STAGE && hasNotMovedOutOfRadius(
                    gpsPoints.subList(i, gpsPoints.size).map { it.location },
                    FIVE_MINUTES_IN_MILLIS)
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

    /**
     * Checks if the user hasn't moved out of a certain radius during a provided interval.
     * The radius is defined by [DIAMETER_OF_GEOFENCE] and does not really define a fix geo-fence
     * or radius around a fix coordinate. It rather defines that there has never been movement
     * with a distance longer than [DIAMETER_OF_GEOFENCE] within the provided interval.
     * The distance in not a total distance but a distance between two locations.
     * The interval is not fix either. It sets the maximum time for travelling the distance.
     * This tries to filter movement that has only occurred within a low radius, like a building.
     *
     * @param locations a list of [Location]s to check.
     * @param interval the duration in which the [DIAMETER_OF_GEOFENCE] distance has to be traveled.
     * @return true if the movement was never far enough duration the intervals, false otherwise.
     */
    private fun hasNotMovedOutOfRadius(locations: List<Location>, interval: Long): Boolean {
        for (i in locations.indices) {
            val currentLocation = locations[i]
            val currentLocationTimestamp = currentLocation.time
            val windowEndTime = currentLocationTimestamp + interval

            for (j in i + 1 until locations.size) {
                val nextLocation = locations[j]

                if (nextLocation.time > windowEndTime) break

                if (currentLocation.distanceTo(nextLocation) > DIAMETER_OF_GEOFENCE) {
                    return true
                }
            }
        }

        return false
    }


    /**
     * Checks if the provided GPS points can be separated into different stages based on speed
     * changes. After a potential stage has reached at least the minimum duration of a stage
     * ([MIN_DURATION_OF_STAGE]) the [predictMode] function is taken for assistance. If the average
     * speed of a prolonged period of time (at least [MIN_DURATION_OF_STAGE]) indicates a different
     * [Mode] as the current stage has been so far, which considers it two separate stages. This
     * may occur when the user walks to the train station and then start driving with the train.
     *
     * @param gpsPoints a list of [LocalGpsPoint]s that should either represent a trip or a
     * potential stage to subdivide.
     * @return a list of lists of [LocalGpsPoint]s, which can be seen as a list of stages. These
     * are the stages the algorithm predicts for the received [LocalGpsPoint]s.
     */
    private suspend fun predictStagesBySpeedChange(gpsPoints: List<LocalGpsPoint>): List<List<LocalGpsPoint>> {
        val stages = mutableListOf<List<LocalGpsPoint>>()
        val currentStage = mutableListOf<LocalGpsPoint>()
        val currentSegment = mutableListOf<LocalGpsPoint>()

        for (i in gpsPoints.indices) {
            val gpsPoint = gpsPoints[i]
            val location = gpsPoints[i].location
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


    /**
     * Predicts the [Mode] of a stage based the average speed and the [PERCENTILE_FOR_PEEK_SPEED]
     * highest speed on average. The average peek speed is taken into consideration because it
     * rules out certain [Mode]s. For example a stage with an average speed of 2 m/s (7.2 km/h)
     * might be predicted as [Mode.WALK] at first. However, if the average peek speed is measured
     * as 10 m/s (36 km/h) [Mode.WALK] can be ruled out. Note that this is the average peek speed
     * not the peek speed. Using the [getPeekSpeed] function we can avoid that anomalies or single
     * inaccurate data points corrupt this prediction. Preferably the data would have been cleaned
     * or smoothed before. It only predicts into [Mode.WALK], [Mode.BICYCLE] and [Mode.CAR_DRIVER]
     * as further extinction is hardly possible just by speed.
     *
     * @param gpsPoints a list of [LocalGpsPoint] that is taken for the prediction
     * @return the predicted [Mode].
     */
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
     * @param locations a list of [Location]s to calculate the average speed of.
     * @return the average speed of the locations in m/s.
     */
    private fun getAverageSpeed(locations: List<Location>): Float {
        var averageSpeed = 0f

        locations.forEach { location ->
            averageSpeed += location.speed
        }

        return averageSpeed / locations.size
    }

    /**
     * Calculates the average speed a the [PERCENTILE_FOR_PEEK_SPEED] highest percentile.
     *
     * @param locations a list of [Location]s the measured it for.
     * @return the average speed of the [PERCENTILE_FOR_PEEK_SPEED] highest percentile.
     */
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
        /**
         * The last percentile for defining the peek speed.
         */
        const val PERCENTILE_FOR_PEEK_SPEED = 0.1 // in percentage

        /**
         * A threshold for what speed is considered as non moving.
         */
        const val STILL_MOTION_THRESHOLD = 0.001f // in meter per second

        /**
         * The maximum duration of a stay in a trip, until it is considered a new trip.
         */
        const val MAX_MOTIONLESS_IN_TRIP = 900000 // 15 minutes in milliseconds

        /**
         * The minimum duration of a trip.
         */
        const val MIN_DURATION_OF_TRIP = 900000 // 15 minutes in milliseconds

        /**
         * The minimum distance to a trip.
         */
        const val MIN_DISTANCE_OF_TRIP = 500f // in meters

        /**
         * The minimum distance a user has to walk apart a stage or trip to be not considered a stay.
         */
        const val DIAMETER_OF_GEOFENCE = 50 // in meters

        /**
         * Five minutes in milliseconds.
         */
        const val FIVE_MINUTES_IN_MILLIS = 300000L // in milliseconds

        /**
         * The minimum duration of a stage.
         */
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