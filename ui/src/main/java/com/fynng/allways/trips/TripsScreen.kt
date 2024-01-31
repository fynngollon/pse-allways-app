package com.fynng.ui.trips

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fynng.allways.trips.TripUiState
import com.fynng.allways.uicomponents.TripCard
import com.pseteamtwo.allways.trip.Mode
import com.pseteamtwo.allways.trip.Purpose


@Composable
fun TripsScreen(
    modifier: Modifier,
    trips: List<TripUiState>
) {
    Surface(
        modifier = modifier.fillMaxSize(),
    ) {
        LazyColumn(
            modifier = modifier,
        ) {
            var year = 0
            var month = 0
            var day = 0

            items(trips) {
                trip: TripUiState ->
                if( year != trip.startYear
                    || month != trip.startMonth
                    || day != trip.startDay
                    ) {

                    Text(
                        text = formatDate(trip.startYear, trip.startMonth, trip.startDay),
                        modifier = modifier
                            .fillMaxWidth()
                            .padding(start = 8.dp, top = 16.dp),
                        fontSize = 20.sp,
                        textAlign = TextAlign.Left,
                        style = MaterialTheme.typography.titleSmall
                        )
                }
                TripCard(
                    modifier = modifier,
                    mode = trip.mode,
                    isConfirmed = trip.isConfirmed,
                    startHour = trip.startHour,
                    startMinute = trip.startMinute,
                    endHour = trip.endHour,
                    endMinute = trip.endMinute,
                    startLocation = trip.startLocationName,
                    endLocation = trip.endLocationName,
                    duration = trip.duration,
                    distance = trip.distance
                )

                year = trip.startYear
                month = trip.startMonth
                day = trip.startDay
            }

//            for (trip in trips) {
//                item {
//                    if (day != trip.startDay || month != trip.startMonth || year != trip.startYear) {
//                        Text(text = "Day changed")
//                    }
//                    TripCard(
//                        modifier = Modifier,
//                        mode = trip.mode,
//                        isConfirmed = trip.isConfirmed,
//                        startHour = trip.startHour,
//                        startMinute = trip.startMinute,
//                        endHour = trip.endHour,
//                        endMinute = trip.endMinute,
//                        startLocation = trip.startLocation,
//                        endLocation = trip.endLocation,
//                        duration = trip.duration,
//                        distance = trip.distance
//                    )
//                    year = trip.startYear
//                    month = trip.startMonth
//                    day = trip.startDay
//                }
//            }
        }
    }
}

fun formatDate(year: Int, month: Int, day: Int): String {
    return String.format("%02d.%02d.%04d", day, month, year)
}

@Preview
@Composable
fun TripsScreenPreview() {
    var trips = listOf(
        TripUiState(
            "1",
            null,
            Purpose.BUSINESS_TRIP,
            Mode.BICYCLE,
            true,
            20,
            1256,
            2024,
            1,
            20,
            13,
            35,
            2024,
            1,
            25,
            13,
            55,
            "1",
            45.0,
            45.0,
            "1",
            45.0,
            45.0
        ),
        TripUiState(
            "2",
            null,
            Purpose.BUSINESS_TRIP,
            Mode.BICYCLE,
            false,
            20,
            1256,
            2024,
            1,
            20,
            14,
            35,
            2024,
            1,
            25,
            14,
            55,
            "2",
            45.0,
            45.0,
            "2",
            45.0,
            45.0
        ),
        TripUiState(
            "3",
            null,
            Purpose.BUSINESS_TRIP,
            Mode.BICYCLE,
            true,
            20,
            1256,
            2024,
            1,
            20,
            13,
            35,
            2024,
            1,
            42,
            13,
            55,
            "KIT",
            45.0,
            45.0,
            "KIT",
            45.0,
            45.0
        ),
        TripUiState(
            "4",
            null,
            Purpose.BUSINESS_TRIP,
            Mode.BICYCLE,
            false,
            20,
            1256,
            2024,
            1,
            21,
            14,
            35,
            2024,
            1,
            26,
            14,
            55,
            "KIT Mensa dfhfdh",
            45.0,
            45.0,
            "KIT",
            45.0,
            45.0
        )


    )
    TripsScreen(
        modifier = Modifier,
        trips = trips
        )
}