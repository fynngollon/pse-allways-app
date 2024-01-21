package com.pseteamtwo.allways.trip

import android.location.Location
import java.time.LocalDateTime

data class Trip(
    var id: String,
    var stages: List<Stage>,
    var purpose: Purpose,
    var isConfirmed: Boolean,
    var startDateTime: LocalDateTime,
    var endDateTime: LocalDateTime,
    var startLocation: Location, //TODO("GeoPoint doesn't exist lol; could create own GeoPoint class but Location would be better if it can be init with missing values")
    var endLocation: Location,
    var duration: Int, //TODO("or java.time.Duration")
    var distance: Int
)
