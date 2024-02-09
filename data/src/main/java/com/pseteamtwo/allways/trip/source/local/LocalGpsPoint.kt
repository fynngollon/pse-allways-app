package com.pseteamtwo.allways.trip.source.local

import android.location.Location
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
// stageId is nullable to allow LocalGpsPoint entries to exist without being associated with a LocalStage.
// However, if a stageId is present, the onDelete = ForeignKey.CASCADE will ensure that when the
// referenced LocalStage is deleted, all associated LocalGpsPoint entries with that stageId will also be deleted.

// Please note that if a LocalGpsPoint is associated with a LocalStage, and you delete that LocalStage,
// all corresponding LocalGpsPoint entries will be removed automatically due to the
// onDelete = ForeignKey.CASCADE configuration.
@Entity(
    tableName = "gps_points",
    foreignKeys = [ForeignKey(
        entity = LocalStage::class,
        parentColumns = ["id"],
        childColumns = ["stageId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class LocalGpsPoint(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    var stageId: Long? = null,
    var location: Location
)