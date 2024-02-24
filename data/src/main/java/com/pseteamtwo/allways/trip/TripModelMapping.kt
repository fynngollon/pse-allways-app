package com.pseteamtwo.allways.trip

import android.location.Location
import com.pseteamtwo.allways.trip.source.local.LocalGpsPoint
import com.pseteamtwo.allways.trip.source.local.LocalStage
import com.pseteamtwo.allways.trip.source.local.LocalStageWithGpsPoints
import com.pseteamtwo.allways.trip.source.local.LocalTripWithStages
import com.pseteamtwo.allways.trip.source.network.NetworkStage
import com.pseteamtwo.allways.trip.source.network.NetworkTrip
import org.osmdroid.util.GeoPoint

/** TODO("kdoc comments are outdated")
 * Data model mapping extension functions. There are three model types for trip, stage and gpsPoint:
 *
 *
 * - TripWithStages: Internal model used to extract dependencies of the database connections.
 * Also used to get information about those database dependencies to the TripAndStageRepository.
 * Also used to convert a LocalTrip into an external Trip.
 *
 * - Trip: External model exposed to other layers in the architecture.
 * Obtained using `toExternal`.
 *
 * - NetworkTrip: Internal model used to represent a trip from the network.
 * Obtained using `toNetwork`.
 *
 * - LocalTrip: Internal model used to represent a trip stored locally in a database.
 * Obtained using `toLocal`.
 *
 *
 * - StageWithGpsPoints: Internal model used to extract dependencies of the database connections.
 * Also used to get information about those database dependencies to the TripAndStageRepository.
 * Also used to convert a LocalStage into an external Stage.
 *
 * - Stage: External model exposed to other layers in the architecture.
 * Obtained using `toExternal`.
 *
 * - NetworkStage: Internal model used to represent a stage from the network.
 * Obtained using `toNetwork`.
 *
 * - LocalStage: Internal model used to represent a stage stored locally in a database.
 * Obtained using `toLocal`.
 *
 *
 * - GpsPoint: External model exposed to other layers in the architecture.
 * Obtained using `toExternal`.
 *
 * - LocalGpsPoint: Internal model used to represent a gpsPoint stored locally in a database.
 * Obtained using `toLocal`.
 *
 *
 * Note: JvmName is used to provide a unique name for each extension function with the same name.
 * Without this, type erasure will cause compiler errors because these methods will have the same
 * signature on the JVM.
 */

/**
 * Trip
 */

/**
 * Trip: local to external
 *
 * Converts a [LocalTripWithStages] into an external [Trip] to expose it to other layers in the architecture.
 *
 * @receiver [LocalTripWithStages]
 */
@JvmName("localToExternal")
fun LocalTripWithStages.toExternal() = Trip(
    id = trip.id,
    purpose = trip.purpose,
    isConfirmed = trip.isConfirmed,
    stages = sortedStages.toExternal(),
)

/**
 * Trip: local to external (List)
 *
 * Converts a list of [LocalTripWithStages]s into a list of external [Trip]s to expose it
 * to other layers in the architecture.
 *
 * @receiver [List]
 */
@JvmName("localTripListToExternal")
fun List<LocalTripWithStages>.toExternal() = map(LocalTripWithStages::toExternal)


/**
 * Trip: local to network
 *
 * Converts a [LocalTripWithStages] into a [NetworkTrip] to upload it to the network database.
 *
 * @receiver [LocalTripWithStages]
 */
@JvmName("localTripWithStagesToNetwork")
fun LocalTripWithStages.toNetwork(): NetworkTrip {
    val trip = this.toExternal()
    return NetworkTrip(
        id = this.trip.id,
        stageIds = this.stages.map { it.stage.id },
        purpose = this.trip.purpose,
        startDateTime = trip.startDateTime,
        endDateTime = trip.endDateTime,
        startLocation = trip.startLocation,
        endLocation = trip.endLocation,
        duration = trip.duration.toInt(),
        distance = trip.distance
    )
}

/**
 * Trip: local to network (List)
 *
 * Converts a list of [LocalTripWithStages]s into a list of [NetworkTrip]
 * to upload it to the network database.
 *
 * @receiver [List]
 */
@JvmName("localTripWithStagesListToNetwork")
fun List<LocalTripWithStages>.toNetwork() = map(LocalTripWithStages::toNetwork)



/**
 * Stage
 */

/**
 * Stage: external to local
 *
 * Converts an external [Stage] into a [LocalStage] to store it into the local database afterwards.
 *
 * @receiver [Stage]
 */
@JvmName("externalStageToLocal")
fun Stage.toLocal(tripId: Long?) = LocalStage(
    id = id,
    tripId = tripId,
    mode = mode
)

/**
 * Stage: external to local (List)
 *
 * Converts a list of external [Stage]s into a list of [LocalStage]s to store it into the
 * local database afterwards.
 *
 * @receiver [List]
 */
@JvmName("externalStageListToLocal")
fun List<Stage>.toLocal(tripId: Long?) = map { stage ->  stage.toLocal(tripId)}

/**
 * Stage: local to external
 *
 * Converts a [LocalStageWithGpsPoints] into an external [Stage] to expose it to
 * other layers in the architecture.
 *
 * @receiver [LocalStageWithGpsPoints]
 */
@JvmName("localStageToExternal")
fun LocalStageWithGpsPoints.toExternal() = Stage(
    id = stage.id,
    mode = stage.mode,
    gpsPoints = sortedGpsPoints.toExternal()
)

/**
 * Stage: local to external (List)
 *
 * Converts a list of [LocalStageWithGpsPoints]s into a list of external [Stage]s to expose it
 * to other layers in the architecture.
 *
 * @receiver [List]
 */
@JvmName("localStageListToExternal")
fun List<LocalStageWithGpsPoints>.toExternal() = map(LocalStageWithGpsPoints::toExternal)


/**
 * Stage: local to network
 *
 * Converts a [LocalStageWithGpsPoints] into a [NetworkStage] to upload it to the network database.
 *
 * @receiver [LocalStageWithGpsPoints]
 */
@JvmName("localStageWithGpsPointsToNetwork")
fun LocalStageWithGpsPoints.toNetwork(): NetworkStage {
    val stage = this.toExternal()
    return NetworkStage(
        id = this.stage.id,
        tripId = this.stage.tripId!!,
        mode = this.stage.mode,
        startDateTime = stage.startDateTime,
        endDateTime = stage.endDateTime,
        startLocation = stage.startLocation,
        endLocation = stage.endLocation,
        duration = stage.duration.toInt(),
        distance = stage.distance
    )
}

/**
 * Stage: local to network (List)
 *
 * Converts a list of [LocalStageWithGpsPoints]s into a list of [NetworkStage]
 * to upload it to the network database.
 *
 * @receiver [List]
 */
@JvmName("localStageWithGpsPointsListToNetwork")
fun List<LocalStageWithGpsPoints>.toNetwork() = map(LocalStageWithGpsPoints::toNetwork)



/**
 * GpsPoint
 */

/**
 * GpsPoint: local to external
 *
 * Converts a [LocalGpsPoint] into an external [GpsPoint] to expose it to
 * other layers in the architecture.
 *
 * @receiver [LocalGpsPoint]
 */
@JvmName("localGpsPointToExternal")
fun LocalGpsPoint.toExternal() = GpsPoint(
    id = id,
    geoPoint = GeoPoint(location),
    time = location.time.convertToLocalDateTime()
)

/**
 * GpsPoint: local to external (List)
 *
 * Converts a list of [LocalGpsPoint]s into a list of external [GpsPoint]s to expose it
 * to other layers in the architecture.
 *
 * @receiver [List]
 */
@JvmName("localGpsPointListToExternal")
fun List<LocalGpsPoint>.toExternal() = map(LocalGpsPoint::toExternal)

/**
 * Converts [GeoPoint] into a [Location].
 *
 * @receiver [GeoPoint]
 */
internal fun GeoPoint.toLocation(time: Long): Location {
    val location = Location("osmdroid")
    location.latitude = this.latitude
    location.longitude = this.longitude
    location.time = time
    location.speed = 0f
    return location
}