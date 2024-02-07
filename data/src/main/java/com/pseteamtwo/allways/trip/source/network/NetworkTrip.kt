package com.pseteamtwo.allways.trip.source.network

import com.pseteamtwo.allways.trip.Purpose
import org.osmdroid.util.GeoPoint
import org.threeten.bp.LocalDateTime

data class NetworkTrip (
    val id: String,
    val stageIds: List<String>,
    val purpose: Purpose,
    val startDateTime: LocalDateTime,
    val endDateTime: LocalDateTime,
    val startLocation: GeoPoint,
    val endLocation: GeoPoint,
    val duration: Int, //TODO("or java.time.Duration")
    val distance: Int
)