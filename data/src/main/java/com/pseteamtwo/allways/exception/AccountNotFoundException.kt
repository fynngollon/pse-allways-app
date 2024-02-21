package com.pseteamtwo.allways.exception

/**
 * Thrown if the user tried to log into an account which is not found on the network database.
 *
 * @constructor Constructs a [AccountNotFoundException] with the
 * specified detail message.
 *
 * @param message The detail message.
 */
class AccountNotFoundException(message: String?) : Exception(message) {
    /**
     * @constructor Constructs a [AccountNotFoundException] with no detail message.
     */
    constructor() : this(null)
}