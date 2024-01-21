package com.pseteamtwo.allways.trip.source.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.pseteamtwo.allways.trip.Purpose
import com.pseteamtwo.allways.trip.Stage
import org.osmdroid.util.GeoPoint
import java.time.LocalDateTime

@Entity(
    tableName = "trips"
)
data class LocalTrip(
    @PrimaryKey val id: String,
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