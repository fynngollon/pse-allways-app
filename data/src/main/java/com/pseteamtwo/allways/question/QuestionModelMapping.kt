package com.pseteamtwo.allways.question

import com.pseteamtwo.allways.question.source.local.LocalQuestion
import com.pseteamtwo.allways.question.source.network.NetworkQuestion

/**
 * Data model mapping extension functions. There are three model types:
 *
 * - Question: External model exposed to other layers in the architecture.
 * Obtained using `toExternal`.
 *
 * - NetworkQuestion: Internal model used to represent a question from the network.
 * Obtained using `toNetwork`.
 *
 * - LocalQuestion: Internal model used to represent a question stored locally in a database.
 * Obtained using `toLocal`.
 *
 *
 * Note: JvmName is used to provide a unique name for each extension function with the same name.
 * Without this, type erasure will cause compiler errors because these methods will have the same
 * signature on the JVM.
 */
//TODO("not sure if JvmName really is necessary")


/**
 * Question: external to local
 *
 * Converts an external [Question] into a [LocalQuestion] to store it
 * into the local database afterwards.
 *
 * @receiver [Question]
 */
fun Question.toLocal() = LocalQuestion(
    id = id,
    title = title,
    type = type,
    options = options,
    answer = answer
)

/**
 * Question: external to local (List)
 *
 * Converts a list of external [Question]s into a list of [LocalQuestion]s to store it
 * into the local database afterwards.
 *
 * @receiver [List]
 */
@JvmName("externalToLocal")
fun List<Question>.toLocal() = map(Question::toLocal)


//local to external
/**
 * Question: local to external
 *
 * Converts a [LocalQuestion] into an external [Question] to expose it
 * to other layers in the architecture.
 *
 * @receiver [LocalQuestion]
 */
fun LocalQuestion.toExternal() = Question(
    id = id,
    title = title,
    type = type,
    options = options,
    answer = answer
)

/**
 * Question: local to external (List)
 *
 * Converts a list of [LocalQuestion]s into a list of external [Question]s to expose it
 * to other layers in the architecture.
 *
 * @receiver [List]
 */
@JvmName("localToExternal")
fun List<LocalQuestion>.toExternal() = map(LocalQuestion::toExternal)


/**
 * Question: network to local
 *
 * Converts a [NetworkQuestion] into a [LocalQuestion] to store it into
 * the local database afterwards.
 *
 * @receiver [NetworkQuestion]
 */
fun NetworkQuestion.toLocal(): LocalQuestion {
    // Handle potential null values for options and answer
    val options = options ?: emptyList()
    return LocalQuestion(
        id = id,
        title = title,
        type = type,
        options = options,
        answer = answer.orEmpty()
    )
}

/**
 * Question: network to local (List)
 *
 * Converts a list of [NetworkQuestion]s into a list of [LocalQuestion]s to store it into
 * the local database afterwards.
 *
 * @receiver [List]
 */
@JvmName("networkToLocal")
fun List<NetworkQuestion>.toLocal() = map(NetworkQuestion::toLocal)

/**
 * Question: local to network
 *
 * Converts a [LocalQuestion] into a [NetworkQuestion] to store it into
 * the network database afterwards.
 *
 * @receiver [LocalQuestion]
 */
fun LocalQuestion.toNetwork(pseudonym: String): NetworkQuestion {
    return NetworkQuestion(
        id = id,
        title = title,
        type = type,
        options = options.ifEmpty { null },
        answer = answer,
        pseudonym = pseudonym
    )
}

/**
 * Question: local to network
 *
 * Converts a list of [LocalQuestion]s into a list of [NetworkQuestion]s to store it into
 * the network database afterwards.
 *
 * @receiver [List]
 */
@JvmName("localToNetwork")
fun List<LocalQuestion>.toNetwork(pseudonym: String) = map { it.toNetwork(pseudonym)}