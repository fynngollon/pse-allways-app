package com.pseteamtwo.allways.trip.source.local

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.pseteamtwo.allways.trip.Purpose

@Entity(
    tableName = "trips"
)
data class LocalTrip(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    var purpose: Purpose,
    var isConfirmed: Boolean,
    var stages: List<LocalStage>
)