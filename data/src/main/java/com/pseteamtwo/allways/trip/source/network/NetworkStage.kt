package com.pseteamtwo.allways.trip.source.network

import com.pseteamtwo.allways.trip.Mode
import org.osmdroid.util.GeoPoint
import java.time.LocalDateTime

data class NetworkStage (
    val id: String,
    val tripId: String,
    val mode: Mode,
    val startDateTime: LocalDateTime,
    val endDateTime: LocalDateTime,
    val startLocation: GeoPoint,
    val endLocation: GeoPoint,
    val duration: Int, //TODO("or java.time.Duration")
    val distance: Int
)