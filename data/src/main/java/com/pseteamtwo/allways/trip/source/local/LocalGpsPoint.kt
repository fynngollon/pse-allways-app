package com.pseteamtwo.allways.trip.source.local

import android.location.Location
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "gpsPoints"
)
data class LocalGpsPoint(
    @PrimaryKey val id: String,
    var location: Location
)