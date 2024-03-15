package com.pseteamtwo.allways.data.trip

import org.threeten.bp.Instant
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId


/**
 * The [ZoneId] which is currently used by the app for all conversions and comparisons with
 * [LocalDateTime] and the epoch time (since 1970-01-01T 00:00:00Z) in milliseconds.
 */
internal val zoneIdOfApp: ZoneId = ZoneId.of("GMT+1")


/**
 * Interprets a [Long] as epoch time (since 1970-01-01T 00:00:00Z) in milliseconds and converts
 * it to a [LocalDateTime] of the current timezone specified in [zoneIdOfApp].
 *
 * @return The converted [LocalDateTime].
 */
internal fun Long.convertToLocalDateTime(): LocalDateTime {
    return LocalDateTime.ofInstant(
        Instant.ofEpochMilli(this),
        zoneIdOfApp
    )
}

/**
 * Converts a [LocalDateTime] to a epoch time (since 1970-01-01T 00:00:00Z) in milliseconds by
 * using the current timezone specified in [zoneIdOfApp].
 *
 * @return The converted epoch time in milliseconds.
 */
internal fun LocalDateTime.convertToMillis(): Long {
    return this.atZone(zoneIdOfApp).toInstant().toEpochMilli();
}


/**
 * Checks if the specified epoch time in milliseconds is in the future at the time this method
 * is called.
 *
 * @param timeInMillis The time to be tested whether it is in the future.
 * @return If the time is in the future, returns true; else false.
 */
internal fun isTimeInFuture(timeInMillis: Long): Boolean {
    return isTimeInFuture(timeInMillis.convertToLocalDateTime())
}

/**
 * Checks if the specified [LocalDateTime] is in the future at the time this method
 * is called.
 *
 * @param localDateTime The time to be tested whether it is in the future.
 * @return If the time is in the future, returns true; else false.
 */
internal fun isTimeInFuture(localDateTime: LocalDateTime): Boolean {
    val currentTime: LocalDateTime = LocalDateTime.now(zoneIdOfApp)
    return localDateTime.isAfter(currentTime)
}