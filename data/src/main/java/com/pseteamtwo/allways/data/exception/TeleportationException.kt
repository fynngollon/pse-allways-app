package com.pseteamtwo.allways.data.exception

/**
 * Thrown if a request by the user to modify [com.pseteamtwo.allways.trip.source.local] trips,
 * stages and gpsPoints of the local databases interferes with the physical logic of space.
 *
 * @constructor Constructs a [TeleportationException] with the
 * specified detail message.
 *
 * @param message The detail message.
 */
class TeleportationException(message: String?) : IllegalArgumentException(message) {
    /**
     * @constructor Constructs a [TeleportationException] with no detail message.
     */
    constructor() : this(null)
}