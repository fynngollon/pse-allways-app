package com.pseteamtwo.allways.data.exception

/**
 * Thrown if a [com.pseteamtwo.allways.question.Question] is to be retrieved
 * (to be modified, deleted, ...), but for the given questionId to do this, there is no
 * such question with that id in the local database.
 *
 * @constructor Constructs a [QuestionIdNotFoundException] with the
 * specified detail message.
 *
 * @param message The detail message.
 */
class QuestionIdNotFoundException(message: String?) : Exception(message) {
    /**
     * @constructor Constructs a [QuestionIdNotFoundException] with no detail message.
     */
    constructor() : this(null)
}