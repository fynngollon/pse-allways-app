package com.pseteamtwo.allways.data.trip.source.local

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.pseteamtwo.allways.data.trip.Mode

/**
 * Representation of a stage for saving as an element of the [androidx.room.Database]
 * [TripAndStageDatabase].
 *
 * A stage is saved with an unique [id] which will be overwritten on the insertion to the
 * database due to it being auto-generated.
 *
 * A stage is saved with a [tripId] to associate it to a [LocalTrip] in the database.
 * This property is nullable to allow [LocalStage] to exist without such association.
 * If [tripId] is present, the database won't allow the existence of the stage without
 * the according trip also existent inside the database already due to the ForeignKey setting.
 * Due to onDelete = ForeignKey.CASCADE, deletion of the trip will lead to all stages with
 * the accordingly set [tripId] to be also deleted.
 *
 * @property id The unique identification number of the trip. Will be auto-generated on
 * database insertion.
 * @property tripId The id of the [LocalTrip] this stage belongs to. If it is null, it has
 * no such trip.
 * @property mode Defines in which mode the stage got travelled in.
 * @constructor Creates a trip with the given properties (id is optional).
 */
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



/**
 * Representation of a stage with gpsPoints for reading a stage of the [androidx.room.Database]
 * [TripAndStageDatabase].
 *
 * Due to the defined [androidx.room.Relation], this object should be read by a [androidx.room.Dao],
 * which constructs this element out of the database while putting in the according
 * [LocalGpsPoint]s.
 *
 * This object is read-only to only be used to get the defined relations of the database between
 * stages and gpsPoints as well as to convert this object to
 * [com.pseteamtwo.allways.trip.Stage] to expose it to other layers of the architecture
 * (especially the ui-layer).
 *
 * @property stage The [LocalStage] itself.
 * @property gpsPoints The [LocalGpsPoint]s this stage consists of.
 * @constructor Creates a stage with gpsPoints with the given properties (this should only be called
 * by according [androidx.room.Dao] objects) due to this class being a read-only class to
 * read from the database.
 */
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