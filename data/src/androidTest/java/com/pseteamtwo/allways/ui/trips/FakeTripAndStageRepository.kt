package com.pseteamtwo.allways.ui.trips

import android.location.Location
import com.pseteamtwo.allways.data.trip.GpsPoint
import com.pseteamtwo.allways.data.trip.Mode
import com.pseteamtwo.allways.data.trip.Purpose
import com.pseteamtwo.allways.data.trip.Stage
import com.pseteamtwo.allways.data.trip.Trip
import com.pseteamtwo.allways.data.trip.repository.TripAndStageRepository
import com.pseteamtwo.allways.data.trip.source.local.LocalGpsPoint
import com.pseteamtwo.allways.data.trip.source.local.LocalStage
import com.pseteamtwo.allways.data.trip.source.local.LocalTrip
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import org.osmdroid.util.GeoPoint
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime

class FakeTripAndStageRepository : TripAndStageRepository {

    /*var gpsPoints: List<GpsPoint> =  listOf(
        GpsPoint(
            id = 0L,
            geoPoint = ,
            time =
        ),
        GpsPoint(
            id = 0L,
            geoPoint = stageUiState.endLocation,
            time = stageUiState.endDateTime
        )
    )*/

   private var trips: MutableStateFlow<List<Trip>> = MutableStateFlow( listOf(Trip(id = 1, purpose = Purpose.BUSINESS_TRIP, isConfirmed = true, stages = listOf(
        Stage(id = 1, mode = Mode.BICYCLE, listOf())
    ))))

    override suspend fun observeAllTrips(): Flow<List<Trip>> {
        return flowOf(listOf())
    }

    override suspend fun observeStagesOfTrip(tripId: Long): Flow<List<Stage>> {
        return flowOf(listOf())
    }

    override suspend fun createTrip(stages: List<Stage>, purpose: Purpose) {
    }

    override suspend fun createTripOfExistingStages(
        localStages: List<LocalStage>,
        purpose: Purpose,
        isCreatedByUser: Boolean
    ): LocalTrip {
        return LocalTrip(id = 1, purpose= Purpose.BUSINESS_TRIP, isConfirmed = false)
    }

    override suspend fun createStageOfExistingGpsPoints(
        localGpsPoints: List<LocalGpsPoint>,
        mode: Mode
    ): LocalStage {
        return LocalStage(mode = mode)
    }

    override suspend fun createGpsPoint(location: Location): LocalGpsPoint {
        return LocalGpsPoint(location = location)
    }

    override suspend fun updateTripPurpose(tripId: Long, purpose: Purpose) {

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
    }

    override suspend fun addUserStageBeforeTripStart(
        tripId: Long,
        mode: Mode,
        startDateTime: LocalDateTime,
        endDateTime: LocalDateTime,
        startLocation: Location
    ) {
    }

    override suspend fun addUserStageAfterTripEnd(
        tripId: Long,
        mode: Mode,
        startDateTime: LocalDateTime,
        endDateTime: LocalDateTime,
        endLocation: Location
    ) {
    }

    override suspend fun separateStageFromTrip(stageId: Long) {
    }

    override suspend fun deleteTrip(tripId: Long) {
    }

    override suspend fun deleteStage(stageId: Long) {
    }

    override suspend fun connectTrips(tripIds: List<Long>) {
    }

    override suspend fun getTripsOfDate(date: LocalDate): List<Trip> {
        return listOf()
    }

    override suspend fun getTripsOfTimespan(
        startTime: LocalDateTime,
        endTime: LocalDateTime
    ): List<Trip> {
        return  listOf()
    }

    override suspend fun saveTripsAndStagesToNetwork(tripIds: List<Long>) {

    }
}