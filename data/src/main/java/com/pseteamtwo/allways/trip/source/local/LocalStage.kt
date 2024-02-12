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
    )]//,
    //indices = [Index(value = ["tripId", "startTime", "endTime"], unique = true)]
)
data class LocalStageWithoutGpsPoints(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    var tripId: Long? = null,
    var mode: Mode,
    //var startTime: Long = 0L,
    //var endTime: Long = 0L
)

data class LocalStage(
    @Embedded val stageData: LocalStageWithoutGpsPoints,
    @Relation(
        parentColumn = "id",
        entityColumn = "stageId"
    )
    var gpsPoints: List<LocalGpsPoint>
) {
    /*
    init {
        if (gpsPoints.isNotEmpty()) {
            stageData.startTime = gpsPoints.first().location.time
            stageData.endTime = gpsPoints.last().location.time
        }
    }
     */
}
