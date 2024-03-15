package com.pseteamtwo.allways.statistics

import android.location.Location
import com.pseteamtwo.allways.trip.Mode
import com.pseteamtwo.allways.trip.Purpose
import com.pseteamtwo.allways.trip.Stage
import com.pseteamtwo.allways.trip.Trip
import com.pseteamtwo.allways.trip.repository.TripAndStageRepository
import kotlinx.coroutines.flow.Flow
import org.osmdroid.util.GeoPoint
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime

class FakeDefaultTripAndStageRepository : TripAndStageRepository {
    override suspend fun observeAllTrips(): Flow<List<Trip>> {
        TODO("Not yet implemented")
    }

    override suspend fun observeStagesOfTrip(tripId: Long): Flow<List<Stage>> {
        TODO("Not yet implemented")
    }

    override suspend fun createTrip(stages: List<Stage>, purpose: Purpose) {
        TODO("Not yet implemented")
    }

    override suspend fun updateTripPurpose(tripId: Long, purpose: Purpose) {
        TODO("Not yet implemented")
    }

    override suspend fun updateStagesOfTrip(
        tripId: Long,
        stageIds: List<Long>,
        modes: List<Mode>,
        startDateTimes: List<LocalDateTime>,
        endDateTimes: List<LocalDateTime>,
        startLocations: List<GeoPoint>,
        endLocations: List<GeoPoint>
    ) {
        TODO("Not yet implemented")
    }

    override suspend fun addUserStageBeforeTripStart(
        tripId: Long,
        mode: Mode,
        startDateTime: LocalDateTime,
        endDateTime: LocalDateTime,
        startLocation: Location
    ) {
        TODO("Not yet implemented")
    }

    override suspend fun addUserStageAfterTripEnd(
        tripId: Long,
        mode: Mode,
        startDateTime: LocalDateTime,
        endDateTime: LocalDateTime,
        endLocation: Location
    ) {
        TODO("Not yet implemented")
    }

    override suspend fun separateStageFromTrip(stageId: Long) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteTrip(tripId: Long) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteStage(stageId: Long) {
        TODO("Not yet implemented")
    }

    override suspend fun connectTrips(tripIds: List<Long>) {
        TODO("Not yet implemented")
    }

    override suspend fun getTripsOfDate(date: LocalDate): List<Trip> {
        TODO("Not yet implemented")
    }

    override suspend fun getTripsOfTimespan(
        startTime: LocalDateTime,
        endTime: LocalDateTime
    ): List<Trip> {
        TODO("Not yet implemented")
    }

    override suspend fun saveTripsAndStagesToNetwork(tripIds: List<Long>) {
        TODO("Not yet implemented")
    }
}