package com.pseteamtwo.allways.trip

import com.pseteamtwo.allways.trip.source.local.LocalGpsPoint
import com.pseteamtwo.allways.trip.source.local.LocalStage
import com.pseteamtwo.allways.trip.source.local.LocalTrip
import com.pseteamtwo.allways.trip.source.network.NetworkStage
import com.pseteamtwo.allways.trip.source.network.NetworkTrip

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

/**
 * Trip
 */
//external to local
fun Trip.toLocal() = LocalTrip(
    id = id,
    stagesId = stages.map { stage -> stage.id },
    purpose = purpose,
    isConfirmed = isConfirmed,
    startDateTime = startDateTime,
    endDateTime = endDateTime,
    startLocation = startLocation,
    endLocation = endLocation,
    duration = duration,
    distance = distance
)

//local to external
fun LocalTrip.toExternal() = Trip(
    id = id,
    stages = emptyList(), //TODO("how to get stages back from ids")
    purpose = purpose,
    isConfirmed = isConfirmed,
    startDateTime = startDateTime,
    endDateTime = endDateTime,
    startLocation = startLocation,
    endLocation = endLocation,
    duration = duration,
    distance = distance
)

//network to local
fun NetworkTrip.toLocal() = LocalTrip(
    id = id,
    stagesId = stagesId,
    purpose = purpose,
    isConfirmed = true, //TODO("on download from network the trip is confirmed, isn't it?")
    startDateTime = startDateTime,
    endDateTime = endDateTime,
    startLocation = startLocation,
    endLocation = endLocation,
    duration = duration,
    distance = distance
)

//local to network
fun LocalTrip.toNetwork() = NetworkTrip(
    id = id,
    stagesId = stagesId,
    purpose = purpose,
    startDateTime = startDateTime,
    endDateTime = endDateTime,
    startLocation = startLocation,
    endLocation = endLocation,
    duration = duration,
    distance = distance
)


/**
 * Stage
 */
//external to local
fun Stage.toLocal() = LocalStage(
    id = id,
    tripId = tripId,
    gpsPointsId = gpsPoints.map { gpsPoint -> gpsPoint.id },
    mode = mode,
    startDateTime = startDateTime,
    endDateTime = endDateTime,
    startLocation = startLocation,
    endLocation = endLocation,
    duration = duration,
    distance = distance
)

//local to external
fun LocalStage.toExternal() = Stage(
    id = id,
    tripId = tripId,
    gpsPoints = emptyList(), //TODO("how to get gpsPoints back from ids"),
    mode = mode,
    startDateTime = startDateTime,
    endDateTime = endDateTime,
    startLocation = startLocation,
    endLocation = endLocation,
    duration = duration,
    distance = distance
)

//network to local
fun NetworkStage.toLocal() = LocalStage(
    id = id,
    tripId = tripId,
    gpsPointsId = emptyList(), //TODO("networkStage has no gpsPoints saved, can it be just empty?")
    mode = mode,
    startDateTime = startDateTime,
    endDateTime = endDateTime,
    startLocation = startLocation,
    endLocation = endLocation,
    duration = duration,
    distance = distance
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


/**
 * GpsPoint
 */
//external to local
fun GpsPoint.toLocal() = LocalGpsPoint(
    id = id,
    location = location
)

//local to external
fun LocalGpsPoint.toExternal() = GpsPoint(
    id = id,
    location = location
)