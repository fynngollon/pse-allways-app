package com.pseteamtwo.allways.ui.uicomponents

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.pseteamtwo.allways.R
import com.pseteamtwo.allways.ui.profile.ProfileViewModel
import com.pseteamtwo.allways.ui.profile.QuestionUiState
import com.pseteamtwo.allways.ui.trips.TripUiState
import com.pseteamtwo.allways.ui.trips.TripsViewModel


/**
 * composable function for displaying the dialog for choosing questions and trips to be sent
 * to the server.
 * @param onDismiss the function to be called when the dialog is dismissed.
 * @param profileViewModel the [ProfileViewModel] containing the information for profile related date
 * @param profileQuestions list of profile related questions
 * @param householdQuestions list of household related questions
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DonateDataDialog(
    onDismiss: () -> Unit,
    profileViewModel: ProfileViewModel,
    profileQuestions: MutableList<QuestionUiState>,
    householdQuestions: MutableList<QuestionUiState>,
    tripsViewModel: TripsViewModel,
    trips: List<TripUiState>
) {

    Dialog(
        onDismissRequest = {onDismiss()},
    ) {
        Card(
            elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
            shape = RoundedCornerShape(15.dp),
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.85f)
                .border(1.dp, color = Color.LightGray, shape = RoundedCornerShape(15.dp))
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row {

                    Button(
                        onClick ={
                            onDismiss()
                            profileViewModel.donateProfileQuestions(profileQuestions)
                            profileViewModel.donateHouseholdQuestions(householdQuestions)
                            tripsViewModel.donateTrips(trips)
                        }


                    ) {
                        Text(text = stringResource(id = R.string.verify))
                    }
                }
                Row {
                    QuestionList(stringResource(id = R.string.profile_questions), questions = profileQuestions)
                }
                Row {
                    QuestionList(stringResource(id = R.string.household_questions), questions = householdQuestions)
                }

                Row {
                    TripList(title = stringResource(id = R.string.trips), trips = trips)
                }
            }
        }
    }
}

/**
 * composable function for displaying a set of questions
 * @param title the title of the set of questions to be displayed
 * @param questions list containing the [QuestionUiState] for each question that is displayed.
 */

//function for displaying a list of questions each with a checkbox indicating if the answer should be send to the server.
@Composable
fun QuestionList(title: String, questions: List<QuestionUiState>) {
    Column {
        Row(modifier = Modifier.padding(start = 10.dp, bottom = 10.dp, top = 20.dp)) {
            Text(text = title)
        }
        Row(modifier = Modifier.padding(top = 10.dp)) {
            LazyColumn() {
                for (question in questions) {
                    item {
                        Row {
                            CheckBox(question = question)
                            Text(text = question.title + ": " + question.answer)
                        }
                    }
                }
            }
        }
    }
}

/**
 * composable function for displaying a set of trips
 * @param title the title of the set of trips to be displayed
 * @param trips list containing the [TripUiState] for each trip that is displayed.
 */

@Composable
fun TripList(title: String, trips: List<TripUiState>) {
    Column {
        Row(modifier = Modifier.padding(start = 10.dp, bottom = 10.dp, top = 20.dp)) {
            Text(text = title)
        }
        Row(modifier = Modifier.padding(top = 10.dp)) {
            LazyColumn() {
                for (trip in trips) {
                    item {
                        Row {
                            TripCheckBox(trip = trip)
                            TripCard(tripUiState = trip)
                        }
                    }
                }
            }
        }
    }
}

/**
 * composable function for displaying a checkbox for a single trip
 * @param trip the trip corresponding to the checkbox
 */

@Composable
fun TripCheckBox(trip: TripUiState) {
    var optionState by remember {
        mutableStateOf(false)
    }
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {

        Checkbox(
            modifier = Modifier
                .size(25.dp),
            checked = optionState,
            onCheckedChange = {
                optionState = it
                trip.sendToServer = it
            },
            colors = CheckboxDefaults.colors(
                uncheckedColor = Color.LightGray,
                checkedColor = Color.LightGray,
                checkmarkColor = Color.Magenta
            )
        )
    }
}


/**
 * composable function for displaying a checkbox for a single question
 * @param question the question corresponding to the checkbox
 */

@Composable
fun CheckBox(question: QuestionUiState) {
    var optionState by remember {
        mutableStateOf(false)
    }
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {

        Checkbox(
            modifier = Modifier
                .size(25.dp),
            checked = optionState,
            onCheckedChange = {
                optionState = it
                question.sendToServer = it
                              },
            colors = CheckboxDefaults.colors(
                uncheckedColor = Color.LightGray,
                checkedColor = Color.LightGray,
                checkmarkColor = Color.Magenta
            )
        )
    }
}






