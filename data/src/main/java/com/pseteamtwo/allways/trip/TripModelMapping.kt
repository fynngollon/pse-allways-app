package com.pseteamtwo.allways.trip

import com.pseteamtwo.allways.trip.source.local.LocalGpsPoint
import com.pseteamtwo.allways.trip.source.local.LocalStage
import com.pseteamtwo.allways.trip.source.local.LocalStageWithGpsPoints
import com.pseteamtwo.allways.trip.source.local.LocalTripWithStages
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
internal fun LocalTripWithStages.toExternal() = Trip(
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
internal fun List<LocalTripWithStages>.toExternal() = map(LocalTripWithStages::toExternal)



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
internal fun Stage.toLocal(tripId: Long?) = LocalStage(
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
internal fun List<Stage>.toLocal(tripId: Long?) = map { stage ->  stage.toLocal(tripId)}

/**
 * Stage: local to external
 *
 * Converts a [LocalStageWithGpsPoints] into an external [Stage] to expose it to
 * other layers in the architecture.
 *
 * @receiver [LocalStageWithGpsPoints]
 */
@JvmName("localStageToExternal")
internal fun LocalStageWithGpsPoints.toExternal() = Stage(
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
internal fun List<LocalStageWithGpsPoints>.toExternal() = map(LocalStageWithGpsPoints::toExternal)



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
internal fun LocalGpsPoint.toExternal() = GpsPoint(
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
internal fun List<LocalGpsPoint>.toExternal() = map(LocalGpsPoint::toExternal)