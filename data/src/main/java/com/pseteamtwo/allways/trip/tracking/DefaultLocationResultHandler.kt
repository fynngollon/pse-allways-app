package com.pseteamtwo.allways.trip.tracking

import android.location.Location
import com.google.android.gms.location.LocationResult
import com.pseteamtwo.allways.di.ApplicationScope
import com.pseteamtwo.allways.trip.repository.TripAndStageRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class DefaultLocationResultHandler @Inject constructor(
    private val tripAndStageRepository: TripAndStageRepository,
    @ApplicationScope private val scope: CoroutineScope
) : LocationResultHandler {

    override fun handleLocationResult(locationResult: LocationResult) {
        val latestLocation = locationResult.lastLocation ?: return

        val locationForGpsPoint = Location(latestLocation.provider)
        locationForGpsPoint.longitude = latestLocation.longitude
        locationForGpsPoint.latitude = latestLocation.latitude
        locationForGpsPoint.time = latestLocation.time
        locationForGpsPoint.speed = latestLocation.speed

        scope.launch {
            tripAndStageRepository.createGpsPoint(locationForGpsPoint)
        }


    }
}