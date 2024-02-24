package com.pseteamtwo.allways.home


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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.pseteamtwo.allways.navigation.Screen
import com.pseteamtwo.allways.profile.ProfileViewModel
import com.pseteamtwo.allways.statistics.DetailedStatisticsCard
import com.pseteamtwo.allways.statistics.StatisticsViewModel
import com.pseteamtwo.allways.uicomponents.DonateDataDialog

@Composable
fun HomeScreen(
    navController: NavController
) {
    var showDonateDataDialog by remember{ mutableStateOf(false) }
    val profileViewModel: ProfileViewModel = hiltViewModel()
    val statisticsViewModel: StatisticsViewModel = hiltViewModel()
    //val tripsViewModel: TripsViewModel = hiltViewModel()
    val statisticsUiState by statisticsViewModel.homeStatisticsUiState.collectAsState()
    val chartUiStates = statisticsUiState.charts


    if (showDonateDataDialog) {
        DonateDataDialog(
            onDismiss = { showDonateDataDialog = false},
            profileViewModel,
            profileQuestions = profileViewModel.profileUiState.value.profileQuestions,
            householdQuestions = profileViewModel.profileUiState.value.householdQuestions)
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
                    Text(text = "Daten spenden")
                }
            }

            Column {
                Button(modifier = Modifier.padding(start = 20.dp, top = 50.dp),
                    onClick = { navController.navigate(route = Screen.Settings.route) }) {
                    Text(text = "Einstellungen")
                }
            }
            
        }

        Row(modifier = Modifier.padding(start = 20.dp, top = 20.dp)) {
            Text(text = "Statistiken", fontSize = 20.sp)
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
                                    labels = chartUiState.labels,
                                    values = chartUiState.values,
                                    title = chartUiState.title,
                                    unit = chartUiState.unit,
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