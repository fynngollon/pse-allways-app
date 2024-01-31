package com.pseteamtwo.allways.trip.repository

import android.location.Location
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

class DefaultTripAndStageRepository : TripAndStageRepository {
    override fun observeAllGpsPoints(): StateFlow<List<GpsPoint>> {
        TODO("Not yet implemented")
    }

    override fun observeAllTrips(): Flow<List<Trip>> {
        TODO("Not yet implemented")
    }

    override suspend fun createTrip(stages: List<Stage>, purpose: Purpose) {
        TODO("Not yet implemented")
    }

    override suspend fun createStage(gpsPoints: List<GpsPoint>, mode: Mode) {
        TODO("Not yet implemented")
    }

    override suspend fun createGpsPoint(location: Location) {
        TODO("Not yet implemented")
    }

    override suspend fun updateTripPurpose(tripId: String, purpose: Purpose) {
        TODO("Not yet implemented")
    }

    override suspend fun updateStage(
        stageId: String,
        mode: Mode,
        startDateTime: LocalDateTime,
        endDateTime: LocalDateTime,
        startLocation: GeoPoint,
        endLocation: GeoPoint
    ) {
        TODO("Not yet implemented")
    }

    override suspend fun addUserStageBeforeTripStart(
        tripId: String,
        mode: Mode,
        startDateTime: LocalDateTime,
        endDateTime: LocalDateTime,
        startLocation: GeoPoint
    ) {
        TODO("Not yet implemented")
    }

    override suspend fun addUserStageAfterTripEnd(
        tripId: String,
        mode: Mode,
        startDateTime: LocalDateTime,
        endDateTime: LocalDateTime,
        endLocation: GeoPoint
    ) {
        TODO("Not yet implemented")
    }

    override suspend fun separateStageFromTrip(stageId: String) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteTrip(tripId: String) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteStage(stageId: String) {
        TODO("Not yet implemented")
    }

    override suspend fun connectTrips(tripIds: List<String>) {
        TODO("Not yet implemented")
    }

    override suspend fun getTripsOfDate(date: Date): List<Trip> {
        TODO("Not yet implemented")
    }

    override suspend fun getTripsOfTimespan(
        startTime: LocalDateTime,
        endTime: LocalDateTime
    ): List<Trip> {
        TODO("Not yet implemented")
    }

    override suspend fun connectTripsAndStages() {
        TODO("Not yet implemented")
    }

    override suspend fun loadTripsAndStagesFromNetwork() {
        TODO("Not yet implemented")
    }

    override suspend fun saveTripsAndStagesToNetwork(tripIds: List<String>) {
        TODO("Not yet implemented")
    }



    private fun createTripId(): String {
        TODO("Not yet implemented")
    }

    private fun createStageId(): String {
        TODO("Not yet implemented")
    }

    private fun createGpsPointId(): String {
        TODO("Not yet implemented")
    }
}