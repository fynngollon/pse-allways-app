package com.pseteamtwo.allways.trip.source.local

import androidx.room.Entity
import androidx.room.PrimaryKey
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
    var stages: List<LocalStage>,
    var purpose: Purpose,
    var isConfirmed: Boolean
)