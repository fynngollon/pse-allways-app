package com.pseteamtwo.allways.trip.source.local

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.pseteamtwo.allways.trip.Purpose

@Entity(
    tableName = "trips"
)
data class LocalTripWithoutStages(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    var purpose: Purpose,
    var isConfirmed: Boolean,
)

data class LocalTrip(
    @Embedded val tripData: LocalTripWithoutStages,
    @Relation(
        parentColumn = "id",
        entityColumn = "tripId"
    )
    var stages: List<LocalStage>
)