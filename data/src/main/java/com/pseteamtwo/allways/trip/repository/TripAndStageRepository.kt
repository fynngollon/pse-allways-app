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

interface TripAndStageRepository {

    suspend fun observeAllTrips(): Flow<List<Trip>>

    suspend fun observeStagesOfTrip(tripId: Long): Flow<List<Stage>>

    suspend fun createTrip(stages: List<Stage>, purpose: Purpose)

    //suspend fun createStage(gpsPoints: List<GpsPoint>, mode: Mode): Stage

    suspend fun createGpsPoint(location: Location): GpsPoint


    suspend fun updateTripPurpose(tripId: Long, purpose: Purpose)

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