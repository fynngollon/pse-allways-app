package com.pseteamtwo.allways.trip

import com.pseteamtwo.allways.trip.source.local.LocalGpsPoint
import com.pseteamtwo.allways.trip.source.local.LocalStage
import com.pseteamtwo.allways.trip.source.local.LocalTrip

/**
 * Data model mapping extension functions. There are three model types for trip, stage and gpsPoint:
 *
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
/* TODO("remove all unnecessary functions and comments")
//external to local
fun Trip.toLocal() = LocalTrip(
    id = id,
    stageIds = stages.map { stage -> stage.id },
    purpose = purpose,
    isConfirmed = isConfirmed,
)
*/


/**
 * Trip: local to external
 *
 * Converts a [LocalTrip] into an external [Trip] to expose it to other layers in the architecture.
 *
 * @receiver [LocalTrip]
 */
@JvmName("localToExternal")
fun LocalTrip.toExternal() = Trip(
    id = id,
    purpose = purpose,
    isConfirmed = isConfirmed,
    stages = stages.toExternal()
)

/**
 * Trip: local to external (List)
 *
 * Converts a list of [LocalTrip]s into a list of external [Trip]s to expose it
 * to other layers in the architecture.
 *
 * @receiver [List]
 */
@JvmName("localTripListToExternal")
fun List<LocalTrip>.toExternal() = map(LocalTrip::toExternal)

/*
//network to local
fun NetworkTrip.toLocal() = LocalTrip(
    id = id,
    stageIds = stageIds,
    purpose = purpose,
    isConfirmed = true
)

//local to network
fun LocalTrip.toNetwork() = NetworkTrip(
    id = id,
    stageIds = stageIds,
    purpose = purpose,
    startDateTime = startDateTime,
    endDateTime = endDateTime,
    startLocation = startLocation,
    endLocation = endLocation,
    duration = duration,
    distance = distance
)
*/

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
fun Stage.toLocal(tripId: Long) = LocalStage(
    id = id,
    tripId = tripId,
    mode = mode,
    gpsPoints = gpsPoints.toLocal(id)
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
fun List<Stage>.toLocal(tripId: Long) = map { stage ->  stage.toLocal(tripId)}

/**
 * Stage: local to external
 *
 * Converts a [LocalStage] into an external [Stage] to expose it to
 * other layers in the architecture.
 *
 * @receiver [LocalStage]
 */
@JvmName("localStageToExternal")
fun LocalStage.toExternal() = Stage(
    id = id,
    gpsPoints = gpsPoints.toExternal(),
    mode = mode,
)

/**
 * Stage: local to external (List)
 *
 * Converts a list of [LocalStage]s into a list of external [Stage]s to expose it
 * to other layers in the architecture.
 *
 * @receiver [List]
 */
@JvmName("localStageListToExternal")
fun List<LocalStage>.toExternal() = map(LocalStage::toExternal)

/*
//network to local
fun NetworkStage.toLocal() = LocalStage(
    id = id,
    tripId = tripId,
    gpsPointIds = emptyList(),
    mode = mode,
    startDateTime = startDateTime,
    endDateTime = endDateTime,
    startLocation = startLocation,
    endLocation = endLocation
)

//local to network
fun LocalStage.toNetwork() = NetworkStage(
    id = id,
    tripId = tripId,
    mode = mode,
    startDateTime = startDateTime,
    endDateTime = endDateTime,
    startLocation = startLocation,
    endLocation = endLocation,
    duration = duration,
    distance = distance
)
*/

/**
 * GpsPoint
 */

/**
 * GpsPoint: external to local
 *
 * Converts an external [GpsPoint] into a [LocalGpsPoint] to store it into the
 * local database afterwards.
 *
 * @receiver [GpsPoint]
 */
@JvmName("externalGpsPointToLocal")
fun GpsPoint.toLocal(stageId: Long) = LocalGpsPoint(
    id = id,
    stageId = stageId,
    location = location
)

/**
 * GpsPoint: external to local (List)
 *
 * Converts a list of external [GpsPoint]s into a list of [LocalGpsPoint]s to store it into the
 * local database afterwards.
 *
 * @receiver [List]
 */
@JvmName("externalGpsPointListToLocal")
fun List<GpsPoint>.toLocal(stageId: Long) = map { it.toLocal(stageId) }

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
    location = location
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