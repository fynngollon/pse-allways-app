package com.pseteamtwo.allways.trip

import org.osmdroid.util.GeoPoint
import java.time.LocalDateTime

data class Stage(
    var id: String,
    var tripId: String,
    var gpsPoints: List<GpsPoint>,
    var mode: Mode,
    var startDateTime: LocalDateTime,
    var endDateTime: LocalDateTime,
    var startLocation: GeoPoint,
    var endLocation: GeoPoint,
    var duration: Int, //TODO("or java.time.Duration")
    var distance: Int
)
