package com.pseteamtwo.allways.data.trip.source.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * This data access object provides functionality to access the [TripAndStageDatabase] in terms
 * of the [LocalStage]s table.
 *
 * According to [androidx.room], room will create an implementation of this interface to provide
 * the functionality of saving to, updating and extracting from the [androidx.room.Database]
 * [TripAndStageDatabase].
 * This functionality is defined for each method in this class by the according
 * [androidx.room] annotations (especially by [androidx.room.Query] commands).
 */
@Dao
interface StageDao {

    /**
     * Observe all stages of the trip specified by the given [tripId].
     *
     * @param tripId Identification number of the trip to observe from the database.
     * @return All stages for the specified trip. If there is no such stage, flow will contain
     * an emptyList.
     */
    @Transaction
    @Query("SELECT * FROM stages WHERE tripId = :tripId")
    fun observeStagesWithGpsPointsForTrip(tripId: Long): Flow<List<LocalStageWithGpsPoints>>

    /**
     * Get the stage specified by the given id.
     *
     * @param stageId Identification number of the stage to observe from the database.
     * @return The requested stage, if present (if not, null).
     */
    @Query("SELECT * FROM stages WHERE id = :stageId")
    suspend fun get(stageId: Long): LocalStage?

    /**
     * Get the stage specified by the given id as a composited object extracted by the room
     * relations defined in [LocalStageWithGpsPoints].
     *
     * @param stageId Identification number of the stage to get from the database.
     * @return The requested stage, if present (if not, null).
     */
    @Transaction
    @Query("SELECT * FROM stages WHERE id = :stageId")
    suspend fun getStageWithGpsPoints(stageId: Long): LocalStageWithGpsPoints?

    /**
     * Returns all stages in the database.
     *
     * @return All stages in the database inside a list.
     */
    @Query("SELECT * FROM stages")
    suspend fun getAll(): List<LocalStage>

    /**
     * Inserts a stage into the database.
     * As [LocalStage.id] is set to be autogenerated, this id will be generated on this insert and
     * is returned by this method.
     *
     * @param stage The trip to be inserted.
     * @return The generated id of the inserted stage.
     */
    @Insert //(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(stage: LocalStage): Long

    /**
     * Updates a stage in the database with the given stage.
     * This will replace the old stage with the new provided stage while this old stage is
     * specified through the id key of the given stage.
     *
     * @param stage The updated stage to be inserted and to replace the old version of this stage.
     */
    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(stage: LocalStage)

    /**
     * Deletes the specified stage out of the database.
     * As the stages table in the database is connected to the gpsPoints table, on this delete,
     * every gpsPoint of this stage will also be deleted.
     *
     * @param stageId The identification number of the stage to be deleted.
     * @return How many stages have been deleted.
     */
    @Query("DELETE FROM stages WHERE id = :stageId")
    suspend fun delete(stageId: Long): Int
}