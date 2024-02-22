package com.pseteamtwo.allways.trip.source.local

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import androidx.room.TypeConverters
import com.pseteamtwo.allways.trip.Purpose
import com.pseteamtwo.allways.typeconverter.ListOfLocalStageConverter

@Entity(
    tableName = "trips"
)
@TypeConverters(ListOfLocalStageConverter::class)
data class LocalTrip(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    var purpose: Purpose,
    var isConfirmed: Boolean
)



data class LocalTripWithStages(
    @Embedded
    val trip: LocalTrip,

    @Relation(
        entity = LocalStage::class,
        parentColumn = "id",
        entityColumn = "tripId"
    )
    val stages: List<LocalStageWithGpsPoints>
)