package com.pseteamtwo.allways.data.trip.source.local

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.pseteamtwo.allways.data.trip.Purpose



/**
 * Representation of a trip for saving as an element of the [androidx.room.Database]
 * [TripAndStageDatabase].
 *
 * A trip is saved with an unique [id] which will be overwritten on the insertion to the
 * database due to it being auto-generated.
 *
 * The property [isConfirmed] has to be false as long as [purpose] is [Purpose.NONE], but also
 * as long as the stages this trip consists of.
 *
 * @property id The unique identification number of the trip. Will be auto-generated on
 * database insertion.
 * @property purpose Defines for which purpose the trip got travelled for.
 * @property isConfirmed Defines if the trip is confirmed by the user (if it was created by the
 * user it should be true; else it should be true after the user provided all necessary information
 * such as [purpose] with valid values).
 * @constructor Creates a trip with the given properties (id is optional).
 */
@Entity(
    tableName = "trips"
)
data class LocalTrip(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    var purpose: Purpose,
    var isConfirmed: Boolean
)



/**
 * Representation of a trip with stages (these stages are stages with gpsPoints) for reading
 * a trip of the [androidx.room.Database] [TripAndStageDatabase].
 *
 * Due to the defined [androidx.room.Relation], this object should be read by a [androidx.room.Dao],
 * which constructs this element out of the database while putting in the according
 * [LocalStageWithGpsPoints]s (and does the same inside those as well).
 *
 * This object is read-only to only be used to get the defined relations of the database between
 * trips, stages and gpsPoints as well as to convert this object to
 * [com.pseteamtwo.allways.trip.Trip] to expose it to other layers of the architecture
 * (especially the ui-layer).
 *
 * @property trip The [LocalTrip] itself.
 * @property stages The [LocalStageWithGpsPoints]s this trip consists of.
 * @constructor Creates a trip with stages with the given properties (this should only be called
 * by according [androidx.room.Dao] objects) due to this class being a read-only class to
 * read from the database.
 */
data class LocalTripWithStages(
    @Embedded
    val trip: LocalTrip,

    @Relation(
        entity = LocalStage::class,
        parentColumn = "id",
        entityColumn = "tripId"
    )
    val stages: List<LocalStageWithGpsPoints> = emptyList()
) {

    val sortedStages: List<LocalStageWithGpsPoints> by lazy {
        val orderedStagesList = stages.toMutableList()
        orderedStagesList.sortBy { it.sortedGpsPoints.first().location.time }
        orderedStagesList.toList()
    }
}