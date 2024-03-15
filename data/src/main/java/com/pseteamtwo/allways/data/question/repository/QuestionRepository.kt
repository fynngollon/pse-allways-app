package com.pseteamtwo.allways.data.question.repository

import com.pseteamtwo.allways.data.exception.QuestionIdNotFoundException
import com.pseteamtwo.allways.data.exception.ServerConnectionFailedException
import com.pseteamtwo.allways.data.question.Question
import kotlinx.coroutines.flow.Flow

/**
 * Repository to handle all [Question]s and to provide according functionality for the ui-layer.
 *
 * This repository provides functionality to retrieve from and save to local and network databases
 * as well as to modify this data of [Question]s.
 */
interface QuestionRepository {

    /**
     * Retrieves all [Question]s saved in the local database.
     *
     * @return A flow of all [Question]s saved in the local database in form of a list.
     */
    fun observeAll(): Flow<List<Question>>

    /**
     * Retrieves the according questionnaire from the network and saves all
     * [Question]s extracted from this into the local database.
     *
     * @throws ServerConnectionFailedException If no connection to the network database can be
     * established.
     */
    @Throws(ServerConnectionFailedException::class)
    suspend fun loadQuestionnaire()

    /**
     * Updates the answer of a [Question] in the local database due to the user providing it.
     *
     * @param id The unique identifier of the question to be updated.
     * @param answer The new answer.
     * @throws QuestionIdNotFoundException If [id] describes a question which is not existent
     * in the local database.
     */
    @Throws(QuestionIdNotFoundException::class)
    suspend fun updateAnswer(id: String, answer: String)

    /**
     * Deletes the [Question] from the local database.
     *
     * @param id The unique identifier of the question to be deleted.
     * @throws QuestionIdNotFoundException If [id] describes a question which is not existent
     * in the local database.
     */
    @Throws(QuestionIdNotFoundException::class)
    suspend fun deleteQuestion(id: String)

    /**
     * Deletes all [Question]s saved in the local database and retrieves all [Question]s
     * saved on the server under the account currently logged in to insert them in the
     * local database instead.
     */
    @Throws(ServerConnectionFailedException::class)
    suspend fun refresh()

    /**
     * Saves all questions of the local database with the specified ids to the network database.
     *
     * @param idList Ids of the questions to be uploaded to the network database.
     */
    @Throws(ServerConnectionFailedException::class)
    suspend fun saveQuestionsToNetwork(idList: List<String>)
}