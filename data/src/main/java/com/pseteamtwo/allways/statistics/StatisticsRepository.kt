package com.pseteamtwo.allways.statistics

import com.pseteamtwo.allways.trip.Mode
import java.time.LocalDateTime
import java.util.Date

interface StatisticsRepository {

    fun getTripDistanceOfAll(): Int

    fun getTripDurationOfAll(): Int

    fun getTripDistanceOfTimespan(startTime: LocalDateTime, endTime: LocalDateTime): Int

    fun getTripDurationOfTimespan(startTime: LocalDateTime, endTime: LocalDateTime): Int

    fun getTripDistanceOfDate(date: Date): Int

    fun getTripDurationOfDate(date: Date): Int


    fun getAverageTripDistance(): Int

    fun getAverageTripDuration(): Int

    fun getAverageTripSpeed(): Int


    fun getModalSplitOfAll(percentaged: Boolean): Map<Mode, Int>

    fun getModalSplitOfTimespan(
        percentaged: Boolean,
        startTime: LocalDateTime,
        endTime: LocalDateTime
    ): Map<Mode, Int>

    fun getModalSplitOfDate(percentaged: Boolean, date: Date): Map<Mode, Int>
}