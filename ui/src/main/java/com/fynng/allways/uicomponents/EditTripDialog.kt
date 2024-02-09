package com.fynng.allways.uicomponents

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.fynng.allways.trips.StageUiState
import com.fynng.allways.trips.TripUiState
import com.fynng.allways.trips.TripsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTripDialog(
    modifier: Modifier = Modifier,
    tripsViewModel: TripsViewModel,
    tripUiState: TripUiState,
) {
    Card(modifier = modifier
        .fillMaxWidth()
        .padding(horizontal = 8.dp)) {
        LazyColumn(
            modifier = modifier
        ) {
            item { Text(
                text = "Weg bearbeiten",
                style = MaterialTheme.typography.titleLarge
            ) }
            items(tripUiState.stageUiStates) {
                stageUiState: StageUiState ->
                val mode by remember { mutableStateOf(stageUiState.mode)}
                val startDateTime by remember { mutableStateOf(stageUiState.startDateTime)}
                val endDateTime by remember { mutableStateOf(stageUiState.endDateTime)}
                val startLocation by remember { mutableStateOf(stageUiState.startLocation)}
                val endLocation by remember { mutableStateOf(stageUiState.endLocation)}
                val startLocationName by remember { mutableStateOf(stageUiState.endLocationName)}
                val endLocationName by remember { mutableStateOf(stageUiState.endLocationName)}

                StageCard(
                    modifier = modifier,
                    stageUiState = stageUiState.copy(
                        mode = mode,
                        startDateTime = startDateTime,
                        endDateTime = endDateTime,
                        startLocation = startLocation,
                        endLocation = endLocation,
                        startLocationName = startLocationName,
                        endLocationName = endLocationName
                    )
                )
            }
            item {
                Row(
                    modifier = modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = { tripsViewModel.updateStages(tripUiState.id) }) {
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun EditTripDialogPreview() {

}
