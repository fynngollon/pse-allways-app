package com.pseteamtwo.allways.uicomponents

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items

import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Delete

import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.SecureFlagPolicy

import com.pseteamtwo.allways.trips.StageUiState
import com.pseteamtwo.allways.trips.TripUiState
import com.pseteamtwo.allways.trips.formatDate

import com.pseteamtwo.allways.trip.Mode
import com.pseteamtwo.allways.trip.Purpose

import kotlinx.coroutines.launch

import org.osmdroid.util.GeoPoint

import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime

/**
 * Composable function to display a Dialog for editing a trip.
 *
 * @param modifier optional composable modifier
 * @param onDismissRequest executes when the user dismisses the dialog
 * @param onConfirm executes when the user confirms their input
 * @param tripUiState the tripUiState of the trip to be edited
 * */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTripDialog(
    modifier: Modifier = Modifier,
    onConfirm: () -> Unit,
    onDismissRequest: () -> Unit,
    tripUiState: TripUiState,
) {

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val stageUiStates = tripUiState.stageUiStates
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
                contentPadding = PaddingValues(top = 4.dp, bottom = 4.dp),
                userScrollEnabled = true
            ) {

                item {
                    Row(
                        modifier = modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (tripUiState.tripId == 0L) "  Neuer Weg" else if(tripUiState.isConfirmed) "  Weg bearbeiten" else "  Weg bestätigen",
                            style = MaterialTheme.typography.titleLarge
                        )
                        IconButton(
                            onClick = {
                                tripUiState.deleteTrip()
                                onDismissRequest()
                            },
                            modifier.scale(1.25f)
                        ) {
                            Icon(imageVector = Icons.Rounded.Delete, contentDescription = "Weg löschen")
                        }
                    }
                }

                item {
                    Column {
                        Spacer(modifier = modifier.height(16.dp))
                        Row(
                            modifier = modifier
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            var expanded by rememberSaveable {
                                mutableStateOf(false)
                            }

                            var selectedPurpose by rememberSaveable {
                                mutableStateOf(tripUiState.purpose)
                            }

                            Text(
                                text = "   Wegezweck:",
                                modifier = modifier.weight(2.2f)
                            )

                            Spacer(modifier = modifier.weight(0.2f))

                            ExposedDropdownMenuBox(
                                expanded = expanded,
                                onExpandedChange = { expanded = !expanded },
                                modifier = modifier.weight(5f)
                            ) {

                                OutlinedTextField(
                                    value = selectedPurpose.name,
                                    onValueChange = {},
                                    readOnly = true,
                                    leadingIcon = {

                                    },
                                    trailingIcon = {
                                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                                    },
                                    shape = RoundedCornerShape(4.dp),
                                    colors = ExposedDropdownMenuDefaults.textFieldColors(),
                                    textStyle = TextStyle.Default.copy(fontSize = 12.sp),
                                    modifier = Modifier
                                        .menuAnchor()
                                        .height(50.dp)
                                        .width(400.dp)
                                )

                                ExposedDropdownMenu(expanded = expanded,
                                    onDismissRequest = { expanded = false }
                                ) {
                                    for(purpose in Purpose.entries) {
                                        DropdownMenuItem(
                                            text = {
                                                Text(purpose.name)
                                            },
                                            onClick = {
                                                expanded = false
                                                selectedPurpose = purpose
                                                tripUiState.setPurpose(purpose)
                                            }
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = modifier.weight(0.2f))
                        }
                        Spacer(modifier = modifier.height(16.dp))
                    }
                }

                if (tripUiState.tripId != 0L) {
                    item {
                        Row(
                            modifier = modifier
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            IconButton(
                                onClick = {
                                    tripUiState.addStageUiStateBefore()
                                },
                                modifier = modifier
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Add, contentDescription = "Weg hinzufügen",
                                )
                            }
                        }
                    }
                }


                var date: LocalDate? = null
                items(stageUiStates) {
                    stageUiState: StageUiState ->
                    val currentDate = stageUiState.startDateTime.toLocalDate()
                    if(date != currentDate) {
                        Column {
                            Spacer(
                                modifier = modifier.height(16.dp)
                            )
                            Row(
                                modifier = modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = formatDate(currentDate),
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                        }

                    }
                    StageCard(
                        modifier = modifier,
                        stageUiState = stageUiState,
                    )
                    date = stageUiState.endDateTime.toLocalDate()
                    if(stageUiState.isLastStageOfTrip) {
                        if(stageUiState.startDateTime.toLocalDate() != stageUiState.endDateTime.toLocalDate()) {
                            Column {
                                Spacer(modifier = modifier.height(4.dp))
                                Row(
                                    modifier = modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = "Ankunft am ",
                                        modifier = modifier.weight(1f),
                                        textAlign = TextAlign.Right,
                                        lineHeight = 8.sp
                                    )
                                    Text(
                                        text = formatDate(stageUiState.endDateTime.toLocalDate()),
                                        modifier = modifier.weight(1f),
                                        textAlign = TextAlign.Center,
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    Spacer(modifier = modifier.weight(1f),)
                                }
                                Spacer(modifier = modifier.height(16.dp))
                            }
                        }
                    }
                }

                if (tripUiState.tripId != 0L) {
                    item {
                        Spacer(modifier = modifier.height(16.dp))
                        Row(
                            modifier = modifier
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            IconButton(
                                onClick = {
                                    tripUiState.addStageUiStateAfter()
                                },
                                modifier = modifier
                                ,
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Add, contentDescription = "Weg hinzufügen",
                                )
                            }
                        }
                    }
                }

                item {
                    Row(
                        modifier = modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(
                            onClick = {
                                if(stageUiStates.any{ it.mode == Mode.NONE} || tripUiState.purpose == Purpose.NONE) {
                                    scope.launch{
                                        val result = snackbarHostState.showSnackbar("Wegezweck oder Verkehrsmittel dürfen nicht leer sein.", null, true, SnackbarDuration.Short)
                                    }
                                } else {
                                    onConfirm()
                                    tripUiState.updateTrip()
                                }
                            },
                            modifier = modifier,
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9549))
                        ) {
                            if(tripUiState.isConfirmed) {
                                Text(text = "Speichern")
                            } else {
                                Text(text = "Bestätigen")
                            }
                        }
                        Spacer(modifier = modifier.width(8.dp))
                    }

                }
            }
            SnackbarHost(
                hostState = snackbarHostState,
            )
        }
    }
}

@Preview
@Composable
fun EditTripDialogPreview() {
    EditTripDialog(
        tripUiState = TripUiState(
            id = 0,
            tripId = 1,
            Purpose.NONE,
            false,
            listOf(
                StageUiState(
                    id = 1,
                    stageId = 0,
                    mode = Mode.NONE,
                    startDateTime = LocalDateTime.of(2024, 1, 20, 1, 30),
                    endDateTime = LocalDateTime.of(2024, 1, 20, 2, 30),
                    isInDatabase = true,
                    isToBeAddedBefore = false,
                    isFirstStageOfTrip = true,
                    isLastStageOfTrip = false,
                    startLocation = GeoPoint(49.001061, 8.413361),
                    endLocation = GeoPoint(49.001061, 8.413361),
                    startLocationName = "Test",
                    endLocationName = "Test",
                    getPreviousStageUiState = {null},
                    getNextStageUiState = {null},
                    setMode = {mode: Mode -> },
                    setStartDate = {},
                    setEndDate = {},
                    setStartTime = {hour: Int, minute: Int -> },
                    setEndTime = {hour: Int, minute: Int -> },
                    setStartLocation = {geoPoint: GeoPoint ->  },
                    setEndLocation = {geoPoint: GeoPoint ->  },
                    setStartLocationName = {locationName: String -> },
                    setEndLocationName = {locationName: String -> },
                ),
                StageUiState(
                    id = 1,
                    stageId = 0,
                    mode = Mode.NONE,
                    isInDatabase = true,
                    isToBeAddedBefore = false,
                    isFirstStageOfTrip = false,
                    isLastStageOfTrip = true,
                    startDateTime = LocalDateTime.of(2024, 1, 21, 1, 30),
                    endDateTime = LocalDateTime.of(2024, 1, 22, 2, 30),
                    startLocation = GeoPoint(49.001061, 8.413361),
                    endLocation = GeoPoint(49.001061, 8.413361),
                    startLocationName = "Test",
                    endLocationName = "Test",
                    getPreviousStageUiState = {null},
                    getNextStageUiState = {null},
                    setMode = {mode: Mode -> },
                    setStartDate = {},
                    setEndDate = {},
                    setStartTime = {hour: Int, minute: Int -> },
                    setEndTime = {hour: Int, minute: Int -> },
                    setStartLocation = {geoPoint: GeoPoint ->  },
                    setEndLocation = {geoPoint: GeoPoint ->  },
                    setStartLocationName = {locationName: String -> },
                    setEndLocationName = {locationName: String -> },
                ),
            ),
            LocalDateTime.MAX,
            LocalDateTime.MAX,
            GeoPoint(49.001061, 8.413361),
            GeoPoint(49.001061, 8.413361),
            "KIT",
            "KIT",
            599,
            4256,
            deleteTrip = {},
            createStageUiStates = {},
            updateTrip = {},
            addStageUiStateBefore = {},
            addStageUiStateAfter = {},
            sendToServer = false,
            setPurpose = {}
        ),
        onConfirm = {},
        onDismissRequest = {}
    )
}
