package com.pseteamtwo.allways.statistics

import com.pseteamtwo.allways.trip.Mode
import com.pseteamtwo.allways.trip.Trip
import com.pseteamtwo.allways.trip.repository.TripAndStageRepository
import kotlinx.coroutines.flow.first
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import java.util.EnumMap
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.roundToInt
import kotlin.math.roundToLong

@Singleton
class DefaultStatisticsRepository @Inject constructor(
    private val tripAndStageRepository: TripAndStageRepository
): StatisticsRepository {

    override suspend fun getTripDistanceOfAll(): Int {
        val trips: List<Trip> = tripAndStageRepository.observeAllTrips().first()
        return trips.sumOf { it.distance }
    }

    override suspend fun getTripDurationOfAll(): Long {
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
    ): Long {
        val trips: List<Trip> = tripAndStageRepository.getTripsOfTimespan(startTime, endTime)
        return trips.sumOf { it.duration }
    }

    override suspend fun getTripDistanceOfDate(date: LocalDate): Int {
        val trips: List<Trip> = tripAndStageRepository.getTripsOfDate(date)
        return trips.sumOf { it.distance }
    }

    override suspend fun getTripDurationOfDate(date: LocalDate): Long {
        val trips: List<Trip> = tripAndStageRepository.getTripsOfDate(date)
        return trips.sumOf { it.duration }
    }


    override suspend fun getAverageTripDistance(): Int {
        val trips: List<Trip> = tripAndStageRepository.observeAllTrips().first()
        return ( trips.sumOf { it.distance } / trips.size.toDouble() ).roundToInt()
    }

    override suspend fun getAverageTripDuration(): Long {
        val trips: List<Trip> = tripAndStageRepository.observeAllTrips().first()
        return ( trips.sumOf { it.duration } / trips.size.toDouble() ).roundToLong()
    }

    override suspend fun getAverageTripSpeed(): Long {
        return ( getAverageTripDistance() / getAverageTripDuration().toDouble() ).roundToLong()
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
        date: LocalDate
    ): EnumMap<Mode, Int> {
        val trips: List<Trip> = tripAndStageRepository.getTripsOfDate(date)
        return getModalSplitOfTrips(trips, percentaged)
    }

    private fun getModalSplitOfTrips(
        trips: List<Trip>,
        percentaged: Boolean
    ): EnumMap<Mode, Int> {
        val modalSplit: EnumMap<Mode, Int> = EnumMap(Mode::class.java) //TODO("not sure if this is correct")

        //set the distance for every mode to 0
        Mode.entries.forEach {
            modalSplit[it] = 0
        }

        //add the distance of every stage to the according mode
        trips.forEach { trip ->
            trip.stages.forEach { stage ->
                val existingDistance = modalSplit[stage.mode] ?: 0
                modalSplit[stage.mode] = existingDistance + stage.distance
            }
        }

        //if modalSplit shall be in per mille, divide distances by totalDistance of all trips
        if(percentaged) { //In per mille
            val totalDistance: Int = trips.sumOf { it.distance }
            //if totalDistance is 0, all modes are already mapped to 0
            if (totalDistance > 0) {
                Mode.entries.forEach { mode ->
                    val existingDistance = modalSplit[mode] ?: 0
                    modalSplit[mode] =
                        (1000 * existingDistance / totalDistance.toDouble()).roundToInt()
                }
            }
        }

        return modalSplit
    }
}