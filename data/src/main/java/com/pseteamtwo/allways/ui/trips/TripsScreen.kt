package com.pseteamtwo.allways.ui.trips

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import androidx.hilt.navigation.compose.hiltViewModel

import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.pseteamtwo.allways.R

import com.pseteamtwo.allways.ui.uicomponents.EditTripDialog
import com.pseteamtwo.allways.ui.uicomponents.TripCard

import org.threeten.bp.LocalDate


/**
 * Composable function to display the TripsScreen of the AllWays app.
 *
 * @param modifier optional composable modifier
 * @param navController navigation controller
 * */
@Composable
fun TripsScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController
) {
    val tripsViewModel: TripsViewModel = hiltViewModel()
    val tripsUiState by tripsViewModel.tripsUiState.collectAsState()
    val tripUiStates = tripsUiState.tripUiStates

    var showAddTripDialog by rememberSaveable { mutableStateOf(false) }
    var addedTripUiStateId by remember {
        mutableLongStateOf(-1L)
    }

    Surface(
        modifier = modifier.fillMaxSize(),
    ) {
        Column {
            if(tripUiStates.isNotEmpty()) {
                Column {
                    Spacer(modifier = modifier.height(8.dp))
                    Row(
                        modifier = modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        IconButton(
                            onClick = {
                                addedTripUiStateId = tripsViewModel.addTrip()
                                showAddTripDialog = true
                            },
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Add,
                                contentDescription = stringResource(id = R.string.add_trip),
                                modifier = modifier.size(48.dp)
                            )
                        }
                    }
                }
                LazyColumn(
                    modifier = modifier,
                    userScrollEnabled = true
                ) {
                    var date: LocalDate? = null
                    items(
                        tripUiStates,
                        //key = { item: TripUiState -> item.id}
                    ) {
                            tripUiState: TripUiState ->
                        if(date != tripUiState.startDateTime.toLocalDate()) {
                            Text(
                                text = formatDate(tripUiState.startDateTime.toLocalDate()),
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
            } else  {
                Column(
                    modifier = modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(id = R.string.no_trips_yet),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = modifier.height(24.dp))
                    IconButton(
                        onClick = {
                            addedTripUiStateId = tripsViewModel.addTrip()
                            showAddTripDialog = true
                        },
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Add,
                            contentDescription = stringResource(id = R.string.add_trip),
                            modifier = modifier.size(48.dp)
                        )
                    }
                    Spacer(modifier = modifier.height(24.dp))
                    Text(
                        text = stringResource(id = R.string.empty_trips_message),
                        modifier.padding(horizontal = 32.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
        if(showAddTripDialog) {
            EditTripDialog(
                modifier = modifier,
                tripUiState = tripsViewModel.getTripUiState(addedTripUiStateId),
                onConfirm = {
                    showAddTripDialog = false
                },
                onDelete = {
                    tripsViewModel.deleteTripUiState(addedTripUiStateId)
                    showAddTripDialog = false
                },
                onDismissRequest = {
                    tripsViewModel.deleteTripUiState(addedTripUiStateId)
                    showAddTripDialog = false
                }
            )
        }
    }
}

fun formatDate(date: LocalDate): String {
    return String.format(
        "%02d.%02d.%04d",
        date.dayOfMonth,
        date.monthValue,
        date.year
    )
}

@Preview
@Composable
fun TripsScreenPreview() {
    TripsScreen(
        navController = rememberNavController()
    )
}