package com.pseteamtwo.allways.statistics

import com.pseteamtwo.allways.trip.Mode
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import java.util.EnumMap

interface StatisticsRepository {

    suspend fun getTripDistanceOfAll(): Int

    suspend fun getTripDurationOfAll(): Long

    suspend fun getTripDistanceOfTimespan(startTime: LocalDateTime, endTime: LocalDateTime): Int

    suspend fun getTripDurationOfTimespan(startTime: LocalDateTime, endTime: LocalDateTime): Long

    suspend fun getTripDistanceOfDate(date: LocalDate): Int

    suspend fun getTripDurationOfDate(date: LocalDate): Long


    suspend fun getAverageTripDistance(): Int

    suspend fun getAverageTripDuration(): Long

    suspend fun getAverageTripSpeed(): Long


    suspend fun getModalSplitOfAll(percentaged: Boolean): EnumMap<Mode, Int>

    suspend fun getModalSplitOfTimespan(
        percentaged: Boolean,
        startTime: LocalDateTime,
        endTime: LocalDateTime
    ): EnumMap<Mode, Int>

    suspend fun getModalSplitOfDate(
        percentaged: Boolean,
        date: LocalDate
    ): EnumMap<Mode, Int>
}