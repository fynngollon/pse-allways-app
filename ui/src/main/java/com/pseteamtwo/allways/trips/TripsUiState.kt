package com.pseteamtwo.allways.trips

/**
 * The UI state of the entirety of the app's [trips][com.pseteamtwo.allways.trip.Trip].
 *
 * @property tripUiStates the trip UI states to all trips. May contain trip UI states for which a
 * trip has yet to be created in the database (e.g. the user is filling in the information for a new
 * trip but hasn't saved it yet).
 * */
data class TripsUiState(
    val tripUiStates: List<TripUiState> = emptyList(),
)
