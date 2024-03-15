package com.pseteamtwo.allways.data.question.source.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

/**
 * This data access object provides functionality to access the [ProfileQuestionDatabase]
 * and the [HouseholdQuestionDatabase] in terms of the [LocalQuestion]s tables.
 *
 * According to [androidx.room], room will create an implementation of this interface to provide
 * the functionality of saving to, updating and extracting from the according
 * [androidx.room.Database]s.
 *
 * This functionality is defined for each method in this class by the according
 * [androidx.room] annotations (especially by [androidx.room.Query] commands).
 */
@Dao
interface QuestionDao {

    /**
     * Observes all questions in the database.
     *
     * @return All questions in the database inside a list in a flow.
     */
    @Query("SELECT * FROM questions")
    fun observeAll(): Flow<List<LocalQuestion>>

    /**
     * Observes the question specified by the given id.
     *
     * @param questionId Identification number of the question to observe from the database.
     * @return A flow of the requested question, if present (if not, flow will contain null).
     */
    @Query("SELECT * FROM questions WHERE id = :questionId")
    fun observe(questionId: String): Flow<LocalQuestion>

    /**
     * Get the question specified by the given id.
     *
     * @param questionId Identification string of the question to get from the database.
     * @return The requested question, if present (if not, null).
     */
    @Query("SELECT * FROM questions WHERE id = :questionId")
    suspend fun get(questionId: String): LocalQuestion?

    /**
     * Does the same as [upsert] for every LocalQuestion in [localQuestions].
     *
     * @param localQuestions The list of questions to be inserted or updated.
     */
    @Upsert
    suspend fun upsertAll(localQuestions: List<LocalQuestion>)

    /**
     * Inserts or updates a question in the database.
     * (Inserts if not in the database; else updates).
     *
     * @param localQuestion The question to be inserted or updated.
     */
    @Upsert
    suspend fun upsert(localQuestion: LocalQuestion)

    /**
     * Deletes all questions inside the database so the database is empty afterwards.
     */
    @Query("DELETE FROM questions")
    suspend fun deleteAll()

    /**
     * Deletes the specified question out of the database.
     *
     * @param questionId The identification number of the question to be deleted.
     * @return How many questions have been deleted.
     */
    @Query("DELETE FROM questions WHERE id = :questionId")
    suspend fun deleteQuestion(questionId: String): Int
}