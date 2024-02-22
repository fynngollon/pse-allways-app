package com.pseteamtwo.allways.trip.source.local

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.pseteamtwo.allways.trip.Mode
import com.pseteamtwo.allways.typeconverter.ListOfLocalGpsPointConverter
import com.pseteamtwo.allways.typeconverter.LocationConverter
import kotlinx.serialization.Contextual
import org.osmdroid.util.GeoPoint
import org.threeten.bp.LocalDateTime

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
@TypeConverters(ListOfLocalGpsPointConverter::class)
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
)