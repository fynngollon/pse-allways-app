package com.pseteamtwo.allways.data.trip.source.local

import android.location.Location
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.pseteamtwo.allways.ui.typeconverter.LocationConverter
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable


/**
 * Representation of a gpsPoint for saving as an element of the [androidx.room.Database]
 * [TripAndStageDatabase].
 *
 * A gpsPoint is saved with an unique [id] which will be overwritten on the insertion to the
 * database due to it being auto-generated.
 *
 * A gpsPoint is saved with a [stageId] to associate it to a [LocalStage] in the database.
 * This property is nullable to allow [LocalGpsPoint] to exist without such association.
 * If [stageId] is present, the database won't allow the existence of the gpsPoint without
 * the according stage also existent inside the database already due to the ForeignKey setting.
 * Due to onDelete = ForeignKey.CASCADE, deletion of the stage will lead to all gpsPoints with
 * the accordingly set [stageId] to be also deleted.
 *
 * @property id The unique identification number of the gpsPoint. Will be auto-generated on
 * database insertion.
 * @property stageId The id of the [LocalStage] this gpsPoints belongs to. If it is null, it has
 * no such stage.
 * @property location The location data of the gpsPoint (especially longitude, latitude and time).
 * @constructor Creates a gpsPoint with the given properties (id and stageId are optional).
 */
@Serializable
@Entity(
    tableName = "gps_points",
    foreignKeys = [ForeignKey(
        entity = LocalStage::class,
        parentColumns = ["id"],
        childColumns = ["stageId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["stageId"])]
)
@TypeConverters(LocationConverter::class)
data class LocalGpsPoint(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    var stageId: Long? = null,
    @Contextual var location: Location
)