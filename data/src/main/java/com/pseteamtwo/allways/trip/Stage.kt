package com.pseteamtwo.allways.trip

import android.location.Location
import java.time.LocalDateTime

data class Stage(
    var id: String,
    var tripId: String,
    var gpsPoints: List<GpsPoint>,
    var mode: Mode,
    var startDateTime: LocalDateTime,
    var endDateTime: LocalDateTime,
    var startLocation: Location, //TODO("GeoPoint doesn't exist lol; could create own GeoPoint class but Location would be better if it can be init with missing values")
    var endLocation: Location,
    var duration: Int, //TODO("or java.time.Duration")
    var distance: Int
)
