package com.pseteamtwo.allways.trip.source.local

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.pseteamtwo.allways.trip.Purpose

@Entity(
    tableName = "trips"
)
internal data class LocalTrip(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    var purpose: Purpose,
    var isConfirmed: Boolean
)



internal data class LocalTripWithStages(
    @Embedded
    val trip: LocalTrip,

    @Relation(
        entity = LocalStage::class,
        parentColumn = "id",
        entityColumn = "tripId"
    )
    val stages: List<LocalStageWithGpsPoints>
) {

    val sortedStages: List<LocalStageWithGpsPoints> by lazy {
        val orderedStagesList = stages.toMutableList()
        orderedStagesList.sortBy { it.sortedGpsPoints.first().location.time }
        orderedStagesList.toList()
    }
}