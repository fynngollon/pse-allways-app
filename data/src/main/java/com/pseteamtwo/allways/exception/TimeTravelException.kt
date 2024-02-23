package com.pseteamtwo.allways.exception

/**
 * Thrown if a request by the user to connect [com.pseteamtwo.allways.trip.source.local] trips,
 * of the local database
 * (in [com.pseteamtwo.allways.trip.repository.TripAndStageRepository.connectTrips])
 * interferes with the physical logic of time.
 *
 * This exception should only be thrown if between any 2 trips to be connected,
 * there is a trip in between which is not to be connected too or
 * if there was a time given for a method in
 * [com.pseteamtwo.allways.trip.repository.TripAndStageRepository] which is in the future.
 *
 * Any other interference with the physical logic of time should throw [NoTimeContinuityException].
 *
 * @constructor Constructs a [TimeTravelException] with the
 * specified detail message.
 *
 * @param message The detail message.
 */
class TimeTravelException(message: String?) : IllegalArgumentException(message) {
    /**
     * @constructor Constructs a [TimeTravelException] with no detail message.
     */
    constructor() : this(null)
}