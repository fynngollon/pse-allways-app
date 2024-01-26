package com.pseteamtwo.allways.trip

import org.osmdroid.util.GeoPoint
import java.time.LocalDateTime

data class Trip(
    val id: String,
    val stages: List<Stage>,
    val purpose: Purpose,
    val isConfirmed: Boolean,
    val startDateTime: LocalDateTime,
    val endDateTime: LocalDateTime,
    val startLocation: GeoPoint,
    val endLocation: GeoPoint,
    val duration: Int, //TODO("or java.time.Duration")
    val distance: Int
)
