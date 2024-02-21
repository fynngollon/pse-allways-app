package com.pseteamtwo.allways.exception

/**
 * Thrown if a request by the user to connect [com.pseteamtwo.allways.trip.source.local] trips,
 * of the local database
 * (in [com.pseteamtwo.allways.trip.repository.TripAndStageRepository.connectTrips])
 * interferes with the physical logic of time.
 *
 * This exception should only be thrown if between any 2 trips to be connected,
 * there is a trip in between which is not to be connected too.
 *
 * Any other interference with the physical logic of time should throw [NoTimeContinuityException].
 *
 * @constructor Constructs a [TimeTravelException] with the
 * specified detail message.
 *
 * @param message The detail message.
 */
class TimeTravelException(message: String?) : Exception(message) {
    /**
     * @constructor Constructs a [TimeTravelException] with no detail message.
     */
    constructor() : this(null)
}