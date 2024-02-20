package com.pseteamtwo.allways.trip.repository

import android.location.Location
import com.pseteamtwo.allways.exception.NoTimeContinuityException
import com.pseteamtwo.allways.exception.ServerConnectionFailedException
import com.pseteamtwo.allways.exception.TeleportationException
import com.pseteamtwo.allways.exception.TimeTravelException
import com.pseteamtwo.allways.trip.GpsPoint
import com.pseteamtwo.allways.trip.Mode
import com.pseteamtwo.allways.trip.Purpose
import com.pseteamtwo.allways.trip.Stage
import com.pseteamtwo.allways.trip.Trip
import kotlinx.coroutines.flow.Flow
import org.osmdroid.util.GeoPoint
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import kotlin.jvm.Throws

/**
 * Repository to handle all [Trip]s, [Stage]s and [GpsPoint]s and
 * to provide according functionality for the ui-layer.
 *
 * This repository provides functionality to retrieve from and save to local and network databases
 * as well as to modify this data of [Trip]s, [Stage]s and [GpsPoint]s while ensuring that these
 * operations do not interfere with physical logic of time and space.
 */
interface TripAndStageRepository {

    /**
     * Retrieves all [Trip]s saved in the local trip database.
     *
     * @return A flow of all [Trip]s saved in the local trip database in form of a list
     */
    suspend fun observeAllTrips(): Flow<List<Trip>>

    /**
     * Retrieves all [Stage]s belonging to the specified [Trip] from the local stage database.
     *
     * @param tripId Identification number of the [Trip], of which the [Stage]s are requested.
     * @return All [Stage]s belonging to the specified [Trip].
     */
    suspend fun observeStagesOfTrip(tripId: Long): Flow<List<Stage>>

    /**
     * Creates a new [Trip] with the provided parameters.
     * Therefore creates a unique id for the new trip and saves it into the local trip database.
     *
     * @param stages The list of [Stage]s which the new trip consists of.
     * @param purpose The purpose of the new trip.
     *
     * TODO("why isn't here a return: Trip as well as on [createGpsPoint]")
     */
    suspend fun createTrip(stages: List<Stage>, purpose: Purpose)

    //suspend fun createStage(gpsPoints: List<GpsPoint>, mode: Mode): Stage

    /**
     * Creates a new [GpsPoint] with the provided [Location].
     * Therefore creates a unique id for the new gpsPoint and saves it
     * into the local gpsPoint database.
     *
     * @param location The [Location] which the new gpsPoint consists of.
     * @return The created gpsPoint.
     */
    suspend fun createGpsPoint(location: Location): GpsPoint


    /**
     * Updates the purpose of the specified [Trip].
     *
     * @param tripId Identification number of the [Trip], of which the purpose should be updated.
     * @param purpose The new purpose.
     */
    suspend fun updateTripPurpose(tripId: Long, purpose: Purpose)

    /**
     * Updates every property of the specified [Stage] except [Stage.id] and [Stage.gpsPoints].
     * Thus, the updated properties aren't computed out of [Stage.gpsPoints] anymore, this list
     * should be set to exactly 2 [GpsPoint]s each containing a [Location] composed out of
     * startTime and startLocation respectively endTime and endLocation.
     * Also ensures, that this updating does not interfere with physical logic of time.
     *
     * @param stageId Identification number of the [Stage].
     * @param mode The new [Mode].
     * @param startTime The new start time.
     * @param endTime The new end time.
     * @param startLocation The new start location.
     * @param endLocation The new end location.
     * @throws NoTimeContinuityException If the provided parameters interfere with physical logic
     * of time, e.g. if the new [startTime] would be during another [Stage] of the
     * local stage database temporally.
     */
    @Throws(NoTimeContinuityException::class)
    suspend fun updateStage(
        stageId: Long,
        mode: Mode,
        startTime: LocalDateTime,
        endTime: LocalDateTime,
        startLocation: GeoPoint,
        endLocation: GeoPoint
    )

    @Throws(NoTimeContinuityException::class)
    suspend fun addUserStageBeforeTripStart(
        tripId: Long,
        mode: Mode,
        startDateTime: LocalDateTime,
        endDateTime: LocalDateTime,
        startLocation: Location
    )

    @Throws(NoTimeContinuityException::class)
    suspend fun addUserStageAfterTripEnd(
        tripId: Long,
        mode: Mode,
        startDateTime: LocalDateTime,
        endDateTime: LocalDateTime,
        endLocation: Location
    )

    suspend fun separateStageFromTrip(stageId: Long)

    suspend fun deleteTrip(tripId: Long)

    suspend fun deleteStage(stageId: Long)

    @Throws(TimeTravelException::class, TeleportationException::class)
    suspend fun connectTrips(tripIds: List<Long>)

    suspend fun getTripsOfDate(date: LocalDate): List<Trip>

    suspend fun getTripsOfTimespan(startTime: LocalDateTime, endTime: LocalDateTime): List<Trip>

    //suspend fun connectTripsAndStages()

    @Throws(ServerConnectionFailedException::class)
    suspend fun loadTripsAndStagesFromNetwork()

    @Throws(ServerConnectionFailedException::class)
    suspend fun saveTripsAndStagesToNetwork(tripIds: List<String>)
}