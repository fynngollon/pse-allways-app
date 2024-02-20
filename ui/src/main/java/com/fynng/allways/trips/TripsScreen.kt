package com.fynng.allways.trips

import android.location.Location
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.fynng.allways.uicomponents.TripCard
import org.threeten.bp.LocalDate



@Composable
fun TripsScreen(
    modifier: Modifier = Modifier,
    tripsViewModel: TripsViewModel,
    navController: NavHostController
) {
    val tripsUiState by tripsViewModel.tripsUiState.collectAsState()

    Surface(
        modifier = modifier.fillMaxSize(),
    ) {
        LazyColumn(
            modifier = modifier,
        ) {
            item {  }
            var date: LocalDate? = null
            items(tripsUiState.tripUiStates) {
                tripUiState: TripUiState ->
                if(date != tripUiState.startDateTime.toLocalDate()) {
                    Text(
                        text = formatDate(
                            tripUiState.startDateTime.year,
                            tripUiState.startDateTime.monthValue,
                            tripUiState.startDateTime.dayOfMonth),
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
                    tripUiState = tripUiState
                )

                date = tripUiState.startDateTime.toLocalDate()
            }
        }
    }
}

fun formatDate(year: Int, month: Int, day: Int): String {
    return String.format("%02d.%02d.%04d", day, month, year)
}

@Preview
@Composable
fun TripsScreenPreview() {
    /*TripsScreen(
        tripsViewModel = TripsViewModel(TestTripAndStageRepository())
    )*/
}