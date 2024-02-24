package com.pseteamtwo.allways.question.source.network

import com.pseteamtwo.allways.exception.ServerConnectionFailedException

/**
 * This class handles all interactions with the database for the questions of a user.
 *
 */
interface QuestionNetworkDataSource {
    /**
     * Load questions searches in the database in the table with the given pseudonym for all
     * Questions that are stored.
     * It creates a connection to a MySql-server and executes the load-sql-statement.
     * The given variables are then added to the NetworkQuestion.
     * The NetworkQuestion is then stored in a list.
     * After all questions are loaded it returns the list of NetworkQuestions.
     * If that did not work a error massage is thrown.
     *
     * @param pseudonym Is used do identify the right table the questions are stored in.
     * @return Is the list of NetWorkQuestions loaded from the table.
     */
    @Throws(ServerConnectionFailedException::class)
    suspend fun loadQuestions(pseudonym: String): List<NetworkQuestion>

    /**
     * Save questions stores all NetworkQuestions from the given list in the database.
     * It creates a connection to a MySql-server and executes the save-sql-statement.
     * If it did not work a error massage will be thrown.
     *
     * @param pseudonym Is used do identify the right table the questions are stored in.
     * @param questions Is the list of NetworkQuestions that is stored in the table
     */
    @Throws(ServerConnectionFailedException::class)
    suspend fun saveQuestions(pseudonym: String, questions: List<NetworkQuestion>)

    /**
     * Delete question deletes the given Question from the database.
     * It creates a connection to a MySql-server and executes the delete-sql-statement.
     * If the action was successful the question is no longer in the database.
     * If not a error massage will be thrown.
     *
     * @param pseudonym Is used do identify the right table the questions are stored in.
     * @param id Is the primary key to find the question in the database.
     */
    suspend fun deleteQuestion(pseudonym: String, id: String)
}