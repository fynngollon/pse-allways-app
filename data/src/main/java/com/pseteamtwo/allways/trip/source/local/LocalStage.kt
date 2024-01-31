package com.pseteamtwo.allways.trip.source.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.pseteamtwo.allways.trip.Mode
import org.osmdroid.util.GeoPoint
import java.time.LocalDateTime

@Entity(
    tableName = "stages"
)
data class LocalStage(
    @PrimaryKey val id: String,
    var tripId: String,
    var gpsPointsId: List<String>,
    var mode: Mode,
    var startDateTime: LocalDateTime,
    var endDateTime: LocalDateTime,
    var startLocation: GeoPoint,
    var endLocation: GeoPoint,
    var duration: Int, //TODO("or java.time.Duration")
    var distance: Int
)