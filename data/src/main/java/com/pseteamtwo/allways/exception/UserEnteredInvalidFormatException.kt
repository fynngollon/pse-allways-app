package com.pseteamtwo.allways.exception

/**
 * Thrown if the user entered an invalid information which is recognized in the data-layer and
 * to be passed to the ui-layer.
 *
 * @constructor Constructs a [UserEnteredInvalidFormatException] with the
 * specified detail message.
 *
 * @param message The detail message.
 */
class UserEnteredInvalidFormatException(message: String?) : Exception(message) {
    /**
     * @constructor Constructs a [UserEnteredInvalidFormatException] with no detail message.
     */
    constructor() : this(null)
}