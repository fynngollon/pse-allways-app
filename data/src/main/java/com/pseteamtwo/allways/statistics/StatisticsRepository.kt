package com.pseteamtwo.allways.statistics

import com.pseteamtwo.allways.trip.Mode
import java.time.LocalDateTime
import java.util.Date
import java.util.EnumMap

interface StatisticsRepository {

    suspend fun getTripDistanceOfAll(): Int

    suspend fun getTripDurationOfAll(): Int

    suspend fun getTripDistanceOfTimespan(startTime: LocalDateTime, endTime: LocalDateTime): Int

    suspend fun getTripDurationOfTimespan(startTime: LocalDateTime, endTime: LocalDateTime): Int

    suspend fun getTripDistanceOfDate(date: Date): Int

    suspend fun getTripDurationOfDate(date: Date): Int


    suspend fun getAverageTripDistance(): Int

    suspend fun getAverageTripDuration(): Int

    suspend fun getAverageTripSpeed(): Int


    suspend fun getModalSplitOfAll(percentaged: Boolean): EnumMap<Mode, Int>

    suspend fun getModalSplitOfTimespan(
        percentaged: Boolean,
        startTime: LocalDateTime,
        endTime: LocalDateTime
    ): EnumMap<Mode, Int>

    suspend fun getModalSplitOfDate(
        percentaged: Boolean,
        date: Date
    ): EnumMap<Mode, Int>
}