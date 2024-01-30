package com.pseteamtwo.allways.trip

import org.osmdroid.util.GeoPoint
import org.threeten.bp.LocalDateTime

data class Stage(
    var id: String,
    var tripId: String,
    var gpsPoints: List<GpsPoint>,
    var mode: Mode,
    var startDateTime: LocalDateTime,
    var endDateTime: LocalDateTime,
    var startLocation: GeoPoint, //TODO("GeoPoint doesn't exist lol; could create own GeoPoint class but Location would be better if it can be init with missing values")
    var endLocation: GeoPoint,
    var duration: Int, //TODO("or java.time.Duration")
    var distance: Int
)
