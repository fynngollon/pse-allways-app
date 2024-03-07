package com.pseteamtwo.allways.exception

/**
 * Thrown if an account is trying to be created with an e-mail which already has an account
 * registered for on the network database.
 *
 * @constructor Constructs a [AccountAlreadyExistsException] with the
 * specified detail message.
 *
 * @param message The detail message.
 */
class AccountAlreadyExistsException(message: String?) : Exception(message) {
    /**
     * @constructor Constructs a [AccountAlreadyExistsException] with no detail message.
     */
    constructor() : this(null)
}