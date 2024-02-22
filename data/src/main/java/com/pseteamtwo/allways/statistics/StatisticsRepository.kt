package com.pseteamtwo.allways.statistics

import com.pseteamtwo.allways.trip.Mode
import com.pseteamtwo.allways.trip.Trip
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import java.util.EnumMap

/**
 * Repository to compute statistics data out of the [Trip]s, [Stage]s and [GpsPoint]s saved in
 * the according local databases.
 *
 * This contains the following statistics:
 * - distances of trips (in different time-spans)
 * - duration of trips (in different time-spans)
 * - average distance of all trips
 * - average duration of all trips
 * - average speed of all trips
 * - modal split of trips (in different time-spans): absolute distance per [Mode] or in per mille
 */
interface StatisticsRepository {

    /**
     * Computes the distance of all [Trip]s together.
     *
     * @return The accumulated distance in meters.
     */
    suspend fun getTripDistanceOfAll(): Int

    /**
     * Computes the duration of all [Trip]s together.
     *
     * @return The accumulated duration in milliseconds.
     */
    suspend fun getTripDurationOfAll(): Long

    /**
     * Computes the distance of all [Trip]s in the specified time-span together.
     * A [Trip] is in the time-span if [Trip.startDateTime] is inside the time-span.
     *
     * @param startTime Beginning of the time-span.
     * @param endTime End of the time-span.
     * @return The accumulated distance in meters.
     */
    suspend fun getTripDistanceOfTimespan(startTime: LocalDateTime, endTime: LocalDateTime): Int

    /**
     * Computes the duration of all [Trip]s in the specified time-span together.
     * A [Trip] is in the time-span if [Trip.startDateTime] is inside the time-span.
     *
     * @param startTime Beginning of the time-span.
     * @param endTime End of the time-span.
     * @return The accumulated duration in milliseconds.
     */
    suspend fun getTripDurationOfTimespan(startTime: LocalDateTime, endTime: LocalDateTime): Long

    /**
     * Computes the distance of all [Trip]s on the specified date together.
     * A [Trip] is on the date if [Trip.startDateTime] is on the date.
     *
     * @param date The date of the trips to be accumulated.
     * @return The accumulated distance in meters.
     */
    suspend fun getTripDistanceOfDate(date: LocalDate): Int

    /**
     * Computes the duration of all [Trip]s on the specified date together.
     * A [Trip] is on the date if [Trip.startDateTime] is on the date.
     *
     * @param date The date of the trips to be accumulated.
     * @return The accumulated duration in milliseconds.
     */
    suspend fun getTripDurationOfDate(date: LocalDate): Long


    /**
     * Computes the distance of all [Trip]s together and divides it by the amount of [Trip]s.
     *
     * @return The average distance per trip in meters.
     */
    suspend fun getAverageTripDistance(): Int

    /**
     * Computes the duration of all [Trip]s together and divides it by the amount of [Trip]s.
     *
     * @return The average duration per trip in milliseconds.
     */
    suspend fun getAverageTripDuration(): Long

    /**
     * Computes the average speed per trip.
     * Therefore should compute [getAverageTripDistance]/[getAverageTripDuration] and round it
     * as well as convert it into meters per second.
     *
     * @return The average speed per trip in meters per second.
     */
    suspend fun getAverageTripSpeed(): Long


    /**
     * Computes the modal split of all [Trip]s together.
     * This means, for every [Mode], how much distance got traveled in that [Mode].
     * Returns it with absolute values if [percentaged] is false and
     * with values in per mille if true.
     *
     * @param percentaged Specifies whether the modal split should be in absolute values
     * or in per mille.
     * @return The modal split (map with either [Mode]->distance or [Mode]->per mille).
     */
    suspend fun getModalSplitOfAll(percentaged: Boolean): EnumMap<Mode, Int>

    /**
     * Computes the modal split of all [Trip]s in the specified time-span together.
     * This means, for every [Mode], how much distance got traveled in that [Mode].
     * Returns it with absolute values if [percentaged] is false and
     * with values in per mille if true.
     *
     * @param percentaged Specifies whether the modal split should be in absolute values
     * or in per mille.
     * @param startTime Beginning of the time-span.
     * @param endTime End of the time-span.
     * @return The modal split (map with either [Mode]->distance or [Mode]->per mille).
     */
    suspend fun getModalSplitOfTimespan(
        percentaged: Boolean,
        startTime: LocalDateTime,
        endTime: LocalDateTime
    ): EnumMap<Mode, Int>

    /**
     * Computes the modal split of all [Trip]s on the specified date together.
     * This means, for every [Mode], how much distance got traveled in that [Mode].
     * Returns it with absolute values if [percentaged] is false and
     * with values in per mille if true.
     *
     * @param percentaged Specifies whether the modal split should be in absolute values
     * or in per mille.
     * @param date The date of the trips to be computed into the modal split.
     * @return The modal split (map with either [Mode]->distance or [Mode]->per mille).
     */
    suspend fun getModalSplitOfDate(
        percentaged: Boolean,
        date: LocalDate
    ): EnumMap<Mode, Int>
}