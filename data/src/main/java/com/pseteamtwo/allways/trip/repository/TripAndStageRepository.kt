package com.pseteamtwo.allways.trip.repository

import android.location.Location
import com.pseteamtwo.allways.exception.NoTimeContinuityException
import com.pseteamtwo.allways.exception.ServerConnectionFailedException
import com.pseteamtwo.allways.exception.TeleportationException
import com.pseteamtwo.allways.exception.TimeTravelException
import com.pseteamtwo.allways.exception.UserEnteredInvalidFormatException
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
     * @return A flow of all [Trip]s saved in the local trip database in form of a list.
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
     * As it is completely created by the user from scratch, the stages and according gpsPoints
     * are also created and saved to the local database and the trip will already set to be
     * confirmed.
     *
     * @param stages The list of [Stage]s which the new trip consists of.
     * @param purpose The purpose of the new trip.
     * @throws UserEnteredInvalidFormatException If the provided list of stages contains
     * invalid informations (times in the future, times interfering with physical logic of time,
     * a stage with mode [Mode.NONE]) or if the trip purpose is [Purpose.NONE].
     *
     * TODO("why isn't here a return: Trip as well as on [createGpsPoint]")
     */
    @Throws(UserEnteredInvalidFormatException::class)
    suspend fun createTrip(stages: List<Stage>, purpose: Purpose)

    //suspend fun createStage(gpsPoints: List<GpsPoint>, mode: Mode): Stage


    /**
     * Updates the purpose of the specified [Trip].
     *
     * @param tripId Identification number of the [Trip], of which the purpose should be updated.
     * @param purpose The new purpose.
     */
    suspend fun updateTripPurpose(tripId: Long, purpose: Purpose)

    /**
     * Updates every property of the specified [Stage] except [Stage.id] and [Stage.gpsPoints].
     * Because the updated properties aren't computed out of [Stage.gpsPoints] anymore, this list
     * should be set to exactly 2 [GpsPoint]s each containing a [Location] composed out of
     * startTime and startLocation respectively endTime and endLocation.
     * This method also ensures that this updating does not interfere with physical logic of time.
     *
     * @param stageId Identification number of the [Stage].
     * @param mode The new [Mode].
     * @param startDateTime The new start time.
     * @param endDateTime The new end time.
     * @param startLocation The new start location.
     * @param endLocation The new end location.
     * @throws NoTimeContinuityException If the provided parameters interfere with physical logic
     * of time, e.g. if the new [startDateTime] would be during another [Stage] of the
     * local stage database temporally.
     */
    @Throws(NoTimeContinuityException::class)
    suspend fun updateStagesOfTrip(
        tripId: Long,
        stageIds: List<Long>,
        modes: List<Mode>,
        startDateTimes: List<LocalDateTime>,
        endDateTimes: List<LocalDateTime>,
        startLocations: List<GeoPoint>,
        endLocations: List<GeoPoint>
    )

    /**
     * Adds a [Stage] given from the user (that means the user provides every property except
     * [Stage.id], [Stage.gpsPoints] and [Stage.endLocation]) to the specified [Trip] temporally
     * before the former first stage of this trip. Thus this method doesn't take
     * [Stage.endLocation] as a parameter as it should be set to the [Stage.startLocation] of the
     * former first stage.
     * This method also ensures that this adding of a stage does not interfere with
     * physical logic of time.
     *
     * @param tripId Identification number of the [Trip] the new stage should be added to.
     * @param mode The new [Mode].
     * @param startDateTime The new start time.
     * @param endDateTime The new end time.
     * @param startLocation The new start location.
     * @throws NoTimeContinuityException If the provided parameters interfere with physical logic
     * of time, e.g. if the new [startDateTime] would be during another [Stage] of the
     * local stage database temporally or if the new [endDateTime] is temporally after the
     * [Stage.startDateTime] of the former first stage of the specified trip.
     */
    @Throws(NoTimeContinuityException::class)
    suspend fun addUserStageBeforeTripStart(
        tripId: Long,
        mode: Mode,
        startDateTime: LocalDateTime,
        endDateTime: LocalDateTime,
        startLocation: Location
    )

    /**
     * Adds a [Stage] given from the user (that means the user provides every property except
     * [Stage.id], [Stage.gpsPoints] and [Stage.endLocation]) to the specified [Trip] temporally
     * after the former last stage of this trip. Thus this method doesn't take
     * [Stage.startLocation] as a parameter as it should be set to the [Stage.endLocation] of the
     * former last stage.
     * This method also ensures that this adding of a stage does not interfere with
     * physical logic of time.
     *
     * @param tripId Identification number of the [Trip] the new stage should be added to.
     * @param mode The new [Mode].
     * @param startDateTime The new start time.
     * @param endDateTime The new end time.
     * @param endLocation The new end location.
     * @throws NoTimeContinuityException If the provided parameters interfere with physical logic
     * of time, e.g. if the new [endDateTime] would be during another [Stage] of the
     * local stage database temporally or if the new [startDateTime] is temporally before the
     * [Stage.endDateTime] of the former last stage of the specified trip.
     */
    @Throws(NoTimeContinuityException::class)
    suspend fun addUserStageAfterTripEnd(
        tripId: Long,
        mode: Mode,
        startDateTime: LocalDateTime,
        endDateTime: LocalDateTime,
        endLocation: Location
    )

    /**
     * Separates the specified [Stage] from the [Trip] containing this stage.
     * As a stage should be part of only 1 trip, this trip should be distinct.
     * As the user should only be able to trigger this event through a trip, this trip exists.
     * After deleting this [Stage] from the according [Trip], this method creates a new [Trip]
     * only containing the separated [Stage].
     *
     * @param stageId Identification number of the [Stage] to be separated.
     */
    suspend fun separateStageFromTrip(stageId: Long)

    /**
     * Deletes the specified [Trip] out of the local trip database.
     *
     * @param tripId Identification number of the [Trip] to be deleted.
     */
    suspend fun deleteTrip(tripId: Long)

    /**
     * Deletes the specified [Stage] out of the local stage database.
     *
     * @param stageId Identification number of the [Stage] to be deleted.
     */
    suspend fun deleteStage(stageId: Long)

    /**
     * Connects all specified [Trip]s to one single [Trip].
     * This method also ensures that this connecting of trips does not interfere with
     * physical logic of time and space.
     *
     * @param tripIds Identification number of the [Trip]s to be connected together.
     * @throws TimeTravelException If the provided trips cannot be connected due to a problem
     * with the physical logic of time. E.g. the provided trips intersect each other temporally.
     * @throws TeleportationException If the provided trips cannot be connected due to a problem
     * with the physical logic of space. E.g. a provided trip ends at a location not matching the
     * start location of the following trip to connect.
     */
    @Throws(TimeTravelException::class, TeleportationException::class)
    suspend fun connectTrips(tripIds: List<Long>)

    /**
     * Returns all [Trip]s of the given [LocalDate]. This means all [Trip]s where
     * [Trip.startDateTime] is temporally on that specified [LocalDate].
     *
     * @param date The [LocalDate] of which the trips are requested.
     * @return All [Trip]s of the given date.
     */
    suspend fun getTripsOfDate(date: LocalDate): List<Trip>

    /**
     * Returns all [Trip]s of the given time-span. This means all [Trip]s where
     * [Trip.startDateTime] is temporally between the specified start and end time.
     *
     * @param startTime The beginning of the time-span.
     * @param endTime The end of the time-span.
     * @return All [Trip]s of the given time-span
     */
    suspend fun getTripsOfTimespan(startTime: LocalDateTime, endTime: LocalDateTime): List<Trip>

    //suspend fun connectTripsAndStages()

    /**
     * Loads all [Trip]s existing on the network database including all their [Stage]s they
     * consist of.
     *
     * @throws ServerConnectionFailedException If no connection to the network database can be
     * established.
     */
    @Throws(ServerConnectionFailedException::class)
    suspend fun loadTripsAndStagesFromNetwork()

    /**
     * Saves all specified [Trip]s to the network database including all their [Stage]s they
     * consist of.
     *
     * @param tripIds The [Trip]s to be saved to the network database.
     * @throws ServerConnectionFailedException If no connection to the network database can be
     * established.
     */
    @Throws(ServerConnectionFailedException::class)
    suspend fun saveTripsAndStagesToNetwork(tripIds: List<String>)
}