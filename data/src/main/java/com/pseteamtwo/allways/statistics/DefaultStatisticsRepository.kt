package com.pseteamtwo.allways.statistics

import com.pseteamtwo.allways.trip.Mode
import java.time.LocalDateTime
import java.util.Date

class DefaultStatisticsRepository : StatisticsRepository {
    override fun getTripDistanceOfAll(): Int {
        TODO("Not yet implemented")
    }

    override fun getTripDurationOfAll(): Int {
        TODO("Not yet implemented")
    }

    override fun getTripDistanceOfTimespan(startTime: LocalDateTime, endTime: LocalDateTime): Int {
        TODO("Not yet implemented")
    }

    override fun getTripDurationOfTimespan(startTime: LocalDateTime, endTime: LocalDateTime): Int {
        TODO("Not yet implemented")
    }

    override fun getTripDistanceOfDate(date: Date): Int {
        TODO("Not yet implemented")
    }

    override fun getTripDurationOfDate(date: Date): Int {
        TODO("Not yet implemented")
    }

    override fun getAverageTripDistance(): Int {
        TODO("Not yet implemented")
    }

    override fun getAverageTripDuration(): Int {
        TODO("Not yet implemented")
    }

    override fun getAverageTripSpeed(): Int {
        return getAverageTripDistance()/getAverageTripDuration()
    }

    override fun getModalSplitOfAll(percentaged: Boolean): Map<Mode, Int> {
        TODO("Not yet implemented")
    }

    override fun getModalSplitOfTimespan(
        percentaged: Boolean,
        startTime: LocalDateTime,
        endTime: LocalDateTime
    ): Map<Mode, Int> {
        TODO("Not yet implemented")
    }

    override fun getModalSplitOfDate(percentaged: Boolean, date: Date): Map<Mode, Int> {
        TODO("Not yet implemented")
    }
}