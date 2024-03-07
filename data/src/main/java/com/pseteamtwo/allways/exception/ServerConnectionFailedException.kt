package com.pseteamtwo.allways.exception

/**
 * Thrown if the connection to the server and therefore
 * to the network database cannot be established.
 *
 * @constructor Constructs a [ServerConnectionFailedException] with the
 * specified detail message.
 *
 * @param message The detail message.
 */
class ServerConnectionFailedException(message: String?) : Exception(message) {
    /**
     * @constructor Constructs a [ServerConnectionFailedException] with no detail message.
     */
    constructor() : this(null)
}