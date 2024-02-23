package com.pseteamtwo.allways.trip.source.network

import com.pseteamtwo.allways.trip.Mode
import org.osmdroid.util.GeoPoint
import org.threeten.bp.LocalDateTime

data class NetworkStage (
    val id: Long,
    val tripId: Long,
    val mode: Mode,
    val startDateTime: LocalDateTime,
    val endDateTime: LocalDateTime,
    val startLocation: GeoPoint,
    val endLocation: GeoPoint,
    val duration: Int,
    val distance: Int
)