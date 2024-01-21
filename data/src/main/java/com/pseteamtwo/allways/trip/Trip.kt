package com.pseteamtwo.allways.trip

import org.osmdroid.util.GeoPoint
import java.time.LocalDateTime
import kotlin.time.Duration

data class Trip(
    var id: String,
    var stages: List<Stage>,
    var purpose: Purpose,
    var isConfirmed: Boolean,
    var startDateTime: LocalDateTime,
    var endDateTime: LocalDateTime,
    var startLocation: GeoPoint,
    var endLocation: GeoPoint,
    var duration: Int, //TODO("or java.time.Duration")
    var distance: Int
)
