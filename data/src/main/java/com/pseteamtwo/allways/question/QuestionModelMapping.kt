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

//external to local
fun Question.toLocal() = LocalQuestion(
    id = id,
    title = title,
    type = type,
    options = options,
    answer = answer
)

@JvmName("externalToLocal")
fun List<Question>.toLocal() = map(Question::toLocal)


//local to external
fun LocalQuestion.toExternal() = Question(
    id = id,
    title = title,
    type = type,
    options = options,
    answer = answer
)

@JvmName("localToExternal")
fun List<LocalQuestion>.toExternal() = map(LocalQuestion::toExternal)


//network to local
fun NetworkQuestion.toLocal() = LocalQuestion(
    id = id,
    title = title,
    type = type,
    options = options,
    answer = answer
)

@JvmName("networkToLocal")
fun List<NetworkQuestion>.toLocal() = map(NetworkQuestion::toLocal)


//local to network
fun LocalQuestion.toNetwork() = NetworkQuestion(
    id = id,
    title = title,
    type = type,
    options = options,
    answer = answer,
    pseudonym = "" //TODO("need pseudonym as parameter in function call?")
)

@JvmName("localToNetwork")
fun List<LocalQuestion>.toNetwork() = map(LocalQuestion::toNetwork)