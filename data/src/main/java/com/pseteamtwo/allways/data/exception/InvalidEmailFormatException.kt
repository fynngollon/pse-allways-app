package com.pseteamtwo.allways.data.exception

/**
 * Thrown if the email specified by the user is not of valid format.
 *
 * @constructor Constructs a [InvalidEmailFormatException] with the
 * specified detail message.
 *
 * @param message The detail message.
 */
class InvalidEmailFormatException(message: String?) : IllegalArgumentException(message) {
    /**
     * @constructor Constructs a [InvalidEmailFormatException] with no detail message.
     */
    constructor() : this(null)
}