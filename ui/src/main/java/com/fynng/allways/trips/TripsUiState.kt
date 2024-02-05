package com.fynng.allways.trips

data class TripsUiState(
    val tripUiStates: List<TripUiState> = emptyList(),
    val loading: Boolean = false,
    val serverConnectionFailed: Boolean = false
)
