package com.pseteamtwo.allways.statistics

import com.pseteamtwo.allways.trip.Mode
import com.pseteamtwo.allways.trip.Trip
import com.pseteamtwo.allways.trip.repository.DefaultTripAndStageRepository
import com.pseteamtwo.allways.trip.repository.TripAndStageRepository
import kotlinx.coroutines.flow.first
import java.time.LocalDateTime
import java.util.Date
import java.util.EnumMap
import kotlin.math.roundToInt

class DefaultStatisticsRepository : StatisticsRepository {
    private val tripAndStageRepository: TripAndStageRepository = DefaultTripAndStageRepository() //TODO("Singleton/Interface: i don't know")


    override suspend fun getTripDistanceOfAll(): Int {
        val trips: List<Trip> = tripAndStageRepository.observeAllTrips().first()
        return trips.sumOf { it.distance }
    }

    override suspend fun getTripDurationOfAll(): Int {
        val trips: List<Trip> = tripAndStageRepository.observeAllTrips().first()
        return trips.sumOf { it.duration }
    }

    override suspend fun getTripDistanceOfTimespan(
        startTime: LocalDateTime,
        endTime: LocalDateTime
    ): Int {
        val trips: List<Trip> = tripAndStageRepository.getTripsOfTimespan(startTime, endTime)
        return trips.sumOf { it.distance }
    }

    override suspend fun getTripDurationOfTimespan(
        startTime: LocalDateTime,
        endTime: LocalDateTime
    ): Int {
        val trips: List<Trip> = tripAndStageRepository.getTripsOfTimespan(startTime, endTime)
        return trips.sumOf { it.duration }
    }

    override suspend fun getTripDistanceOfDate(date: Date): Int {
        val trips: List<Trip> = tripAndStageRepository.getTripsOfDate(date)
        return trips.sumOf { it.distance }
    }

    override suspend fun getTripDurationOfDate(date: Date): Int {
        val trips: List<Trip> = tripAndStageRepository.getTripsOfDate(date)
        return trips.sumOf { it.duration }
    }


    override suspend fun getAverageTripDistance(): Int {
        val trips: List<Trip> = tripAndStageRepository.observeAllTrips().first()
        return ( trips.sumOf { it.distance } / trips.size.toDouble() ).roundToInt()
    }

    override suspend fun getAverageTripDuration(): Int {
        val trips: List<Trip> = tripAndStageRepository.observeAllTrips().first()
        return ( trips.sumOf { it.duration } / trips.size.toDouble() ).roundToInt()
    }

    override suspend fun getAverageTripSpeed(): Int {
        return ( getAverageTripDistance() / getAverageTripDuration().toDouble() ).roundToInt()
    }


    override suspend fun getModalSplitOfAll(percentaged: Boolean): EnumMap<Mode, Int> {
        val trips: List<Trip> = tripAndStageRepository.observeAllTrips().first()
        return getModalSplitOfTrips(trips, percentaged)
    }

    override suspend fun getModalSplitOfTimespan(
        percentaged: Boolean,
        startTime: LocalDateTime,
        endTime: LocalDateTime
    ): EnumMap<Mode, Int> {
        val trips: List<Trip> = tripAndStageRepository.getTripsOfTimespan(startTime, endTime)
        return getModalSplitOfTrips(trips, percentaged)
    }

    override suspend fun getModalSplitOfDate(
        percentaged: Boolean,
        date: Date
    ): EnumMap<Mode, Int> {
        val trips: List<Trip> = tripAndStageRepository.getTripsOfDate(date)
        return getModalSplitOfTrips(trips, percentaged)
    }

    private fun getModalSplitOfTrips(
        trips: List<Trip>,
        percentaged: Boolean
    ): EnumMap<Mode, Int> {
        val modalSplit: EnumMap<Mode, Int> = EnumMap(Mode::class.java) //TODO("not sure if this is correct")

        Mode.entries.forEach {
            modalSplit[it] = 0
        }

        trips.forEach {
            it.stages.forEach {
                modalSplit.merge(it.mode, it.distance)
                { prevVal: Int, distance: Int -> prevVal + distance }
            }
        }

        if(percentaged) { //In per mille
            val totalDistance: Int = trips.sumOf { it.distance }
            Mode.entries.forEach {
                modalSplit.merge(it, totalDistance)
                { prevVal: Int, divisor: Int -> (1000 * prevVal / divisor.toDouble()).roundToInt() }
            }
        }

        return modalSplit
    }
}