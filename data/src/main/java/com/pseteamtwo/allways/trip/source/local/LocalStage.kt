package com.pseteamtwo.allways.trip.source.local

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.pseteamtwo.allways.trip.Mode
import org.osmdroid.util.GeoPoint
import org.threeten.bp.LocalDateTime

@Entity(tableName = "stages",
    foreignKeys = [ForeignKey(
        entity = LocalTrip::class,
        parentColumns = ["id"],
        childColumns = ["tripId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class LocalStage(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    var tripId: Long? = null,
    var mode: Mode,
    var gpsPoints: List<LocalGpsPoint>
)
