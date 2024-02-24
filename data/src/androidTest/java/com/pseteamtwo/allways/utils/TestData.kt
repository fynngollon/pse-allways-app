package com.pseteamtwo.allways.utils

import android.location.Location
import com.pseteamtwo.allways.trip.GpsPoint
import com.pseteamtwo.allways.trip.Mode
import com.pseteamtwo.allways.trip.Stage
import com.pseteamtwo.allways.trip.convertToLocalDateTime
import org.osmdroid.util.GeoPoint
import org.threeten.bp.LocalDateTime


val geoPoint1 = GeoPoint(0.000, 0.001)
val geoPoint2 = GeoPoint(0.000, 0.002)
val geoPoint3 = GeoPoint(0.000, 0.002)
val geoPoint4 = GeoPoint(0.000, 0.003)

const val TIME_1: Long = 0
const val TIME_2: Long = 10
const val TIME_3: Long = 20
const val TIME_4: Long = 50

val stage1 = Stage(
    1,
    Mode.WALK,
    listOf(GpsPoint(1, geoPoint1, TIME_1.convertToLocalDateTime()),
        GpsPoint(2, geoPoint2, TIME_2.convertToLocalDateTime()))
)
val stage2 = Stage(
    2,
    Mode.MOTORCYCLE,
    listOf(GpsPoint(3, geoPoint3, TIME_3.convertToLocalDateTime()),
        GpsPoint(4, geoPoint4, TIME_4.convertToLocalDateTime()))
)


private fun GeoPoint.toLocation(time: Long): Location {
    val location = Location("osmdroid")
    location.latitude = this.latitude
    location.longitude = this.longitude
    location.time = time
    location.speed = 0f
    return location
}