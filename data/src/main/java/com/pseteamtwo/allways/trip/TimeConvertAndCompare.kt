package com.pseteamtwo.allways.trip

import org.threeten.bp.Instant
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId





val zoneIdOfApp: ZoneId = ZoneId.systemDefault()



internal fun Long.convertToLocalDateTime(): LocalDateTime {
    return LocalDateTime.ofInstant(
        Instant.ofEpochMilli(this),
        zoneIdOfApp
    )
}

internal fun LocalDateTime.convertToMillis(): Long {
    return this.atZone(zoneIdOfApp).toInstant().toEpochMilli();
}



internal fun isTimeInFuture(timeInMillis: Long): Boolean {
    return isTimeInFuture(timeInMillis.convertToLocalDateTime())
}

internal fun isTimeInFuture(localDateTime: LocalDateTime): Boolean {
    val currentTime: LocalDateTime = LocalDateTime.now(ZoneId.systemDefault())
    return localDateTime.isAfter(currentTime)
}