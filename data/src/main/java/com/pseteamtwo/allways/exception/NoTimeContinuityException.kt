package com.pseteamtwo.allways.exception

/**
 * Thrown if a request by the user to modify [com.pseteamtwo.allways.trip.source.local] trips,
 * stages and gpsPoints of the local databases interferes with the physical logic of time.
 *
 * @constructor Constructs a [NoTimeContinuityException] with the
 * specified detail message.
 *
 * @param message The detail message.
 */
class NoTimeContinuityException(message: String?) : Exception(message) {
    /**
     * @constructor Constructs a [NoTimeContinuityException] with no detail message.
     */
    constructor() : this(null)
}