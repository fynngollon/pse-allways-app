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
import kotlinx.coroutines.flow.StateFlow
import org.osmdroid.util.GeoPoint
import java.time.LocalDateTime
import java.util.Date
import kotlin.jvm.Throws

interface TripAndStageRepository {
    fun observeAllGpsPoints(): StateFlow<List<GpsPoint>>

    fun observeAllTrips(): Flow<List<Trip>>

    suspend fun createTrip(stages: List<Stage>, purpose: Purpose)

    suspend fun createStage(gpsPoints: List<GpsPoint>, mode: Mode)

    suspend fun createGpsPoint(location: Location)


    suspend fun updateTripPurpose(tripId: String, purpose: Purpose)

    @Throws(NoTimeContinuityException::class)
    suspend fun updateStage(
        stageId: String,
        mode: Mode,
        startDateTime: LocalDateTime,
        endDateTime: LocalDateTime,
        startLocation: GeoPoint,
        endLocation: GeoPoint
    )

    @Throws(NoTimeContinuityException::class)
    suspend fun addUserStageBeforeTripStart(
        tripId: String,
        mode: Mode,
        startDateTime: LocalDateTime,
        endDateTime: LocalDateTime,
        startLocation: GeoPoint
    )

    @Throws(NoTimeContinuityException::class)
    suspend fun addUserStageAfterTripEnd(
        tripId: String,
        mode: Mode,
        startDateTime: LocalDateTime,
        endDateTime: LocalDateTime,
        endLocation: GeoPoint
    )

    suspend fun separateStageFromTrip(stageId: String)

    suspend fun deleteTrip(tripId: String)

    suspend fun deleteStage(stageId: String)

    @Throws(TimeTravelException::class, TeleportationException::class)
    suspend fun connectTrips(tripIds: List<String>)

    suspend fun getTripsOfDate(date: Date): List<Trip>

    suspend fun getTripsOfTimespan(startTime: LocalDateTime, endTime: LocalDateTime): List<Trip>

    suspend fun connectTripsAndStages()

    @Throws(ServerConnectionFailedException::class)
    suspend fun loadTripsAndStagesFromNetwork()

    @Throws(ServerConnectionFailedException::class)
    suspend fun saveTripsAndStagesToNetwork(tripIds: List<String>)
}