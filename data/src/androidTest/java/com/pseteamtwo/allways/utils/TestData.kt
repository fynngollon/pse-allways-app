package com.pseteamtwo.allways.utils

import android.location.Location
import com.pseteamtwo.allways.trip.GpsPoint
import com.pseteamtwo.allways.trip.Mode
import com.pseteamtwo.allways.trip.Stage
import org.osmdroid.util.GeoPoint



val geoPoint1 = GeoPoint(0.000, 0.001)
val geoPoint2 = GeoPoint(0.000, 0.002)
val geoPoint3 = GeoPoint(0.000, 0.002)
val geoPoint4 = GeoPoint(0.000, 0.003)

val location1 = geoPoint1.toLocation(0)
val location2 = geoPoint2.toLocation(10)
val location3 = geoPoint3.toLocation(10)
val location4 = geoPoint4.toLocation(30)

val stage1 = Stage(
    1,
    Mode.WALK,
    listOf(GpsPoint(1, location1), GpsPoint(2, location2))
)
val stage2 = Stage(
    2,
    Mode.MOTORCYCLE,
    listOf(GpsPoint(3, location3), GpsPoint(4, location4))
)


private fun GeoPoint.toLocation(time: Long): Location {
    val location = Location("osmdroid")
    location.latitude = this.latitude
    location.longitude = this.longitude
    location.time = time
    location.speed = 0f
    return location
}