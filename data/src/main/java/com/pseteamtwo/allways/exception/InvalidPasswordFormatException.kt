package com.pseteamtwo.allways.exception

/**
 * Thrown if the password specified by the user is not of valid format meaning it is empty or does
 * not meet security standards.
 *
 * @constructor Constructs a [InvalidPasswordFormatException] with the
 * specified detail message.
 *
 * @param message The detail message.
 */
class InvalidPasswordFormatException(message: String?) : IllegalArgumentException(message) {
    /**
     * @constructor Constructs a [InvalidPasswordFormatException] with no detail message.
     */
    constructor() : this(null)
}