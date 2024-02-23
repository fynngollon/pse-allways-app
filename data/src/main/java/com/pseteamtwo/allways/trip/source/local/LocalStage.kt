package com.pseteamtwo.allways.trip.source.local

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.pseteamtwo.allways.trip.Mode

@Entity(tableName = "stages",
    foreignKeys = [
        ForeignKey(
            entity = LocalTrip::class,
            parentColumns = ["id"],
            childColumns = ["tripId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["tripId"])]
)
data class LocalStage(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    var tripId: Long? = null,
    var mode: Mode
)



data class LocalStageWithGpsPoints(
    @Embedded
    val stage: LocalStage,

    @Relation(
        parentColumn = "id",
        entityColumn = "stageId"
    )
    val gpsPoints: List<LocalGpsPoint>
) {

    val sortedGpsPoints: List<LocalGpsPoint> by lazy {
        val orderedGpsPointsList = gpsPoints.toMutableList()
        orderedGpsPointsList.sortBy { it.location.time }
        orderedGpsPointsList.toList()
    }
}