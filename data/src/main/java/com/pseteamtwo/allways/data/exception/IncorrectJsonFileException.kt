package com.pseteamtwo.allways.data.exception

/**
 * Thrown if the JSON-file used for decoding the questions from the server in
 * [com.pseteamtwo.allways.question.repository.QuestionRepository.loadQuestionnaire] is
 * not in the right format and therefore cannot be decoded.
 *
 * @constructor Constructs a [IncorrectJsonFileException] with the
 * specified detail message.
 *
 * @param message The detail message.
 */
class IncorrectJsonFileException(message: String?) : Exception(message) {
    /**
     * @constructor Constructs a [IncorrectJsonFileException] with no detail message.
     */
    constructor() : this(null)
}