package com.pseteamtwo.allways.statistics

import com.pseteamtwo.allways.trip.Mode
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import java.util.EnumMap

class FakeStatisticsRepository: StatisticsRepository {
    override suspend fun getTripDistanceOfAll(): Int {
        return 50
    }

    override suspend fun getTripDurationOfAll(): Long {
        return 50
    }

    override suspend fun getTripDistanceOfTimespan(
        startTime: LocalDateTime,
        endTime: LocalDateTime
    ): Int {
        return 50
    }

    override suspend fun getTripDurationOfTimespan(
        startTime: LocalDateTime,
        endTime: LocalDateTime
    ): Long {
        return 50
    }

    override suspend fun getTripDistanceOfDate(date: LocalDate): Int {
        return 50
    }

    override suspend fun getTripDurationOfDate(date: LocalDate): Long {
        return 50
    }

    override suspend fun getAverageTripDistance(): Int {
        return 50
    }

    override suspend fun getAverageTripDuration(): Long {
        return 50
    }

    override suspend fun getAverageTripSpeed(): Long {
        return 50
    }

    override suspend fun getModalSplitOfAll(percentaged: Boolean): EnumMap<Mode, Int> {
        val modalSplit: EnumMap<Mode, Int> = EnumMap(Mode::class.java)
        modalSplit[Mode.WALK] = 10
        modalSplit[Mode.REGIONAL_BUS] = 5
        modalSplit[Mode.OTHER] = 1


        return modalSplit

    }

    override suspend fun getModalSplitOfTimespan(
        percentaged: Boolean,
        startTime: LocalDateTime,
        endTime: LocalDateTime
    ): EnumMap<Mode, Int> {
        val modalSplit: EnumMap<Mode, Int> = EnumMap(Mode::class.java)
        modalSplit[Mode.WALK] = 10
        modalSplit[Mode.REGIONAL_BUS] = 5
        modalSplit[Mode.OTHER] = 1
        return modalSplit
    }

    override suspend fun getModalSplitOfDate(
        percentaged: Boolean,
        date: LocalDate
    ): EnumMap<Mode, Int> {
        val modalSplit: EnumMap<Mode, Int> = EnumMap(Mode::class.java)
        modalSplit[Mode.WALK] = 10
        modalSplit[Mode.REGIONAL_BUS] = 5
        modalSplit[Mode.OTHER] = 1
        return modalSplit
    }
}