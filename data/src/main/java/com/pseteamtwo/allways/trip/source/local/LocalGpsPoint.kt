package com.pseteamtwo.allways.trip.source.local

import android.location.Location
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Ignore
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.pseteamtwo.allways.typeconverter.LocationConverter
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

// stageId is nullable to allow LocalGpsPoint entries to exist without being associated with a LocalStage.
// However, if a stageId is present, the onDelete = ForeignKey.CASCADE will ensure that when the
// Please note that if a LocalGpsPoint is associated with a LocalStage, and you delete that LocalStage,
// all corresponding LocalGpsPoint entries will be removed automatically due to the
// onDelete = ForeignKey.CASCADE configuration.
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

/*
@Serializable
data class LocationWrapper(
    val provider: String,
    val latitude: Double,
    val longitude: Double,
    val time: Long,
    val speed: Float
) {
    fun toLocation(): Location {
        val location = Location(provider)
        location.latitude = latitude
        location.longitude = longitude
        location.time = time
        location.speed = speed
        return location
    }

    companion object {
        fun fromLocation(location: Location): LocationWrapper {
            return LocationWrapper(
                provider = location.provider.orEmpty(),
                longitude = location.longitude,
                latitude = location.latitude,
                time = location.time,
                speed = location.speed
            )
        }
    }
}

class LocationWrapperConverter {
    @TypeConverter
    fun fromLocation(location: LocationWrapper): String {
        return "${location.provider},${location.latitude},${location.longitude},${location.time},${location.speed}"
    }

    @TypeConverter
    fun toLocation(locationString: String): LocationWrapper {
        val parts = locationString.split(",")
        return LocationWrapper(
            provider = parts[0],
            latitude = parts[1].toDouble(),
            longitude = parts[2].toDouble(),
            time = parts[3].toLong(),
            speed = parts[4].toFloat()
        )
    }
}

 */

