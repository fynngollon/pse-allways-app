package com.fynng.allways.uicomponents

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.SecureFlagPolicy
import com.fynng.allways.trips.StageUiState
import com.fynng.allways.trips.TripUiState

import com.pseteamtwo.allways.trip.Mode
import com.pseteamtwo.allways.trip.Purpose
import org.osmdroid.util.GeoPoint
import org.threeten.bp.LocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTripDialog(
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit,
    tripUiState: TripUiState,
) {
    Dialog(
        onDismissRequest = {onDismissRequest()},
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            securePolicy = SecureFlagPolicy.Inherit,
            usePlatformDefaultWidth = false
        )
    ) {
        Card(modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)) {
            LazyColumn(
                modifier = modifier,
                contentPadding = PaddingValues(top = 4.dp, bottom = 4.dp)
            ) {
                item { Text(
                    text = if(tripUiState.isConfirmed) " Weg bearbeiten" else " Weg bestätigen",
                    style = MaterialTheme.typography.titleLarge
                ) }
                item {
                    Row(
                        modifier = modifier.fillMaxWidth(),
                    ) {
                        Spacer(modifier = modifier.weight(0.05f))
                        Button(
                            onClick = {
                                onDismissRequest()
                                tripUiState.updateTrip
                            },
                            modifier = modifier.weight(0.4f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(text = "+")
                        }
                        Spacer(modifier = modifier.weight(2.05f))
                    }
                }
                items(tripUiState.stageUiStates) {
                        stageUiState: StageUiState ->
                    StageCard(
                        modifier = modifier,
                        stageUiState = stageUiState
                    )
                }
                item {
                    Row(
                        modifier = modifier.fillMaxWidth(),
                    ) {
                        Spacer(modifier = modifier.weight(0.05f))
                        Button(
                            onClick = {
                                tripUiState.addStageUiStateAfter
                            },
                            modifier = modifier.weight(0.4f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(text = "+")
                        }
                        Spacer(modifier = modifier.weight(1f))
                        Button(
                            onClick = {
                                onDismissRequest()
                                tripUiState.updateTrip
                            },
                            modifier = modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            if(tripUiState.isConfirmed) {
                                Text(text = "Speichern")
                            } else {
                                Text(text = "Bestätigen")
                            }
                        }
                        Spacer(modifier = modifier.weight(0.05f))
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun EditTripDialogPreview() {
    EditTripDialog(
        tripUiState = TripUiState(
            1,
            listOf(
                StageUiState(
                    id = 1,
                    mode = Mode.NONE,
                    startDateTime = LocalDateTime.MAX,
                    endDateTime = LocalDateTime.MAX,
                    startLocation = GeoPoint(49.001061, 8.413361),
                    endLocation = GeoPoint(49.001061, 8.413361),
                    startLocationName = "Test",
                    endLocationName = "Test",
                    setMode = {mode: Mode -> },
                    setStartTime = {hour: Int, minute: Int -> },
                    setEndTime = {hour: Int, minute: Int -> },
                    setStartLocation = {geoPoint: GeoPoint ->  },
                    setEndLocation = {geoPoint: GeoPoint ->  },
                    setStartLocationName = {locationName: String -> },
                    setEndLocationName = {locationName: String -> },
                    updateStage = {}
                ),
                StageUiState(
                    id = 1,
                    mode = Mode.NONE,
                    startDateTime = LocalDateTime.MAX,
                    endDateTime = LocalDateTime.MAX,
                    startLocation = GeoPoint(49.001061, 8.413361),
                    endLocation = GeoPoint(49.001061, 8.413361),
                    startLocationName = "Test",
                    endLocationName = "Test",
                    setMode = {mode: Mode -> },
                    setStartTime = {hour: Int, minute: Int -> },
                    setEndTime = {hour: Int, minute: Int -> },
                    setStartLocation = {geoPoint: GeoPoint ->  },
                    setEndLocation = {geoPoint: GeoPoint ->  },
                    setStartLocationName = {locationName: String -> },
                    setEndLocationName = {locationName: String -> },
                    updateStage = {}
                ),
            ),
            Purpose.NONE,
            Mode.NONE,
            false,
            LocalDateTime.MAX,
            LocalDateTime.MAX,
            GeoPoint(49.001061, 8.413361),
            GeoPoint(49.001061, 8.413361),
            "KIT",
            "KIT",
            599,
            4256,
            createStageUiStates = {},
            updateTrip = {},
            addStageUiStateAfter = {}
        ),
        onDismissRequest = {}
    )
}
