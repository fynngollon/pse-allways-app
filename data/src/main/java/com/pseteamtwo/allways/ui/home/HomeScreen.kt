package com.pseteamtwo.allways.ui.home


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.pseteamtwo.allways.R
import com.pseteamtwo.allways.ui.navigation.Screen
import com.pseteamtwo.allways.ui.profile.ProfileViewModel
import com.pseteamtwo.allways.ui.statistics.ChartContent
import com.pseteamtwo.allways.ui.statistics.DetailedStatisticsCard
import com.pseteamtwo.allways.ui.statistics.StatisticsViewModel
import com.pseteamtwo.allways.ui.trips.TripsViewModel
import com.pseteamtwo.allways.ui.uicomponents.DonateDataDialog

/**
 * composable function for displaying the screen of the home view of the application.
 * @param navController thze navcontroller for navigation
 */

@Composable
fun HomeScreen(
    navController: NavController
) {
    var showDonateDataDialog by remember{ mutableStateOf(false) }
    val profileViewModel: ProfileViewModel = hiltViewModel()
    val statisticsViewModel: StatisticsViewModel = hiltViewModel()
    val statisticsUiState by statisticsViewModel.homeStatisticsUiState.collectAsState()
    val chartUiStates = statisticsUiState.charts

    val tripsViewModel: TripsViewModel = hiltViewModel()
    val tripsUiState by tripsViewModel.tripsUiState.collectAsState()
    val tripsUiStates = tripsUiState.tripUiStates


    if (showDonateDataDialog) {
        DonateDataDialog(
            onDismiss = { showDonateDataDialog = false},
            profileViewModel,
            profileQuestions = profileViewModel.profileUiState.value.profileQuestions,
            householdQuestions = profileViewModel.profileUiState.value.householdQuestions,
            trips = tripsUiStates,
            tripsViewModel = tripsViewModel
        )
    }

    Column {

        Row {
            Column {
                Button(
                    modifier = Modifier.padding(start = 20.dp, top = 50.dp),
                    onClick = {
                        showDonateDataDialog = true
                    }
                ) {
                    Text(text = stringResource(id = R.string.spend_data))
                }
            }

            Column {
                Button(modifier = Modifier.padding(start = 20.dp, top = 50.dp),
                    onClick = { navController.navigate(route = Screen.Settings.route) }) {
                    Text(text = stringResource(id = R.string.settings))
                }
            }
        }

        Row(modifier = Modifier.padding(start = 20.dp, top = 20.dp)) {
            Text(text = stringResource(id = R.string.statistics), fontSize = 20.sp)
        }

        Row {
            LazyColumn {
                for (chartUiState in chartUiStates) {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = Color.White
                            ),
                            elevation = CardDefaults.cardElevation(
                                defaultElevation = 6.dp
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 10.dp, top = 10.dp)
                        ) {
                            Row(modifier = Modifier.padding(top = 40.dp)) {
                                DetailedStatisticsCard(
                                    labels =
                                    when (chartUiState.contentType) {
                                        ChartContent.DISTANCE_LAST_WEEK -> chartUiState.labels.map { "$it." }
                                        else -> chartUiState.labels.map { stringResource(id = it) }
                                    },
                                    values = chartUiState.values,
                                    title = chartUiState.contentType.getTitleForChartContent(),
                                    unit = chartUiState.contentType.getUnitForChartContent(),
                                    type = chartUiState.type
                                )
                            }
                        }
                    }
                }


            }
        }
    }
}