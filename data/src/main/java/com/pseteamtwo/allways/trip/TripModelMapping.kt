package com.pseteamtwo.allways.trip

import com.pseteamtwo.allways.trip.source.local.LocalGpsPoint
import com.pseteamtwo.allways.trip.source.local.LocalStage
import com.pseteamtwo.allways.trip.source.local.LocalTrip

/**
 * Data model mapping extension functions. There are three model types:
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
//TODO("not sure if JvmName really is necessary")

/*
/**
 * Trip
 */
//external to local
fun Trip.toLocal() = LocalTrip(
    id = id,
    stageIds = stages.map { stage -> stage.id },
    purpose = purpose,
    isConfirmed = isConfirmed,
)
*/

//local to external
fun LocalTrip.toExternal() = Trip(
    id = id,
    purpose = purpose,
    isConfirmed = isConfirmed,
    stages = stages.toExternal()
)

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
//external to local
fun Stage.toLocal(tripId: Long) = LocalStage(
    id = id,
    tripId = tripId,
    mode = mode,
    gpsPoints = gpsPoints.toLocal(id)
)

fun List<Stage>.toLocal(tripId: Long) = map { stage ->  stage.toLocal(tripId)}

//local to external
fun LocalStage.toExternal() = Stage(
    id = id,
    gpsPoints = gpsPoints.toExternal(),
    mode = mode,
)

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
//external to local

fun GpsPoint.toLocal(stageId: Long) = LocalGpsPoint(
    id = id,
    stageId = stageId,
    location = location
)

fun List<GpsPoint>.toLocal(stageId: Long) = map { it.toLocal(stageId) }

//local to external
fun LocalGpsPoint.toExternal() = GpsPoint(
    id = id,
    location = location
)

fun List<LocalGpsPoint>.toExternal() = map(LocalGpsPoint::toExternal)