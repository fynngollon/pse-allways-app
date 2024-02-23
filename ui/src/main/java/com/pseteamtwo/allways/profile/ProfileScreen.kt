package com.pseteamtwo.allways.profile


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.pseteamtwo.allways.question.QuestionType


@Composable
fun ProfileScreen(
    navController: NavController,
    ) {

    val profileViewModel: ProfileViewModel = hiltViewModel()
    val questionItemModifier: Modifier = Modifier.padding(bottom = 25.dp, start = 20.dp, top = 10.dp)
    val cardTitleSize = 20
    val questionTitleSize = 18

    val profile by profileViewModel.profileUiState.collectAsState()
    val profileQuestions = profile.profileQuestions

    val householdQuestions = profile.householdQuestions




    LazyColumn(
        modifier = Modifier.padding(20.dp)
    ) {
        item {
            Row(modifier = Modifier.padding(bottom = 20.dp)) {
                QuestionsCard("Persönliche Fragen",
                    questions = profileQuestions,
                    modifier = questionItemModifier,
                    cardTitleSize,
                    questionTitleSize
                )
            }
        }

        item {
            Row {
                QuestionsCard("Haushaltsfragen",
                    questions = householdQuestions,
                    modifier = questionItemModifier,
                    cardTitleSize,
                    questionTitleSize
                )
            }
        }
        item {
            Row(
                verticalAlignment = Alignment.Bottom
            ) {
                Box() {
                    Button(
                        modifier = Modifier.padding(top = 50.dp),
                        onClick = {
                            for (question in profileQuestions) {
                                profileViewModel.updateProfileAnswer(question.id, question.answer)
                            }
                            for (question in householdQuestions) {
                                profileViewModel.updateHouseholdAnswer(question.id, question.answer)
                            }
                        }) {
                        Text(text = "Änderungen speichern")
                    }
                }
            }
        }
    }
}

@Composable
fun QuestionsCard(title: String,
                  questions: List<QuestionUiState>,
                  modifier: Modifier,
                  cardTitleSize: Int,
                  questionTitleSize: Int) {

    Card(){
        Column {

            Row(modifier = modifier) {
                Text(text = title, style = LocalTextStyle.current.copy(fontSize = 20.sp) )
            }
            Row {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding()
                ) {
                    for (question in questions) {

                            Row(

                            ) {
                                QuestionItem(question = question, modifier = modifier, cardTitleSize, questionTitleSize)
                            }

                    }
                }
            }
        }
    }
}

@Composable
fun QuestionItem(question: QuestionUiState, modifier: Modifier, cardTitleSize: Int, questionTitleSize: Int) {

    when (question.type) {
        QuestionType.TEXT -> TextQuestion(
            title = question.title,
            answer = question.answer,
            question,
            modifier,
            questionTitleSize
        )

        QuestionType.CHECKBOX -> CheckBoxQuestion(

            title = question.title,
            question,
            options = question.options,
            answer = question.answer,
            modifier,
            questionTitleSize
        )

        QuestionType.SPINNER -> SpinnerQuestion(
            title = question.title,
            question,
            options = question.options,
            answer = question.answer,
            modifier,
            questionTitleSize
        )

        else -> {}
    }
}

@Composable
fun TextQuestion(title: String,
                 answer: String,
                 question: QuestionUiState,
                 modifier: Modifier,
                 questionTitleSize: Int
) {
    var text by remember { mutableStateOf(answer) }
    Column(modifier = modifier) {
        Row {
            Text(text = title, style = LocalTextStyle.current.copy(fontSize = questionTitleSize.sp))
        }
        Row(modifier = Modifier.padding(top = 8.dp)) {
            OutlinedTextField(

                value = text,
                onValueChange = {
                    text = it
                    question.answer = it
                },
            )
        }
        
    }
}

@Composable
fun RadioButtonQuestion(title: String, options: List<String>, answer: String) {

}

@Composable
fun CheckBoxQuestion(
    title: String,
    question: QuestionUiState,
    options: List<String>,
    answer: String,
    modifier: Modifier,
    questionTitleSize: Int
) {
    Column(modifier = modifier) {
        Text(text = title, style = LocalTextStyle.current.copy(fontSize = questionTitleSize.sp))
        for(option in options) {
            CheckBox(option)
        }
    }
}

@Composable
fun CheckBox(option: String) {
    var myState by remember {
        mutableStateOf(false)
    }
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = option)
        Checkbox(
            modifier = Modifier
                .size(25.dp),
            checked = myState,
            onCheckedChange = { myState = it },
            colors = CheckboxDefaults.colors(
                uncheckedColor = Color.LightGray,
                checkedColor = Color.LightGray,
                checkmarkColor = Color.Magenta
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpinnerQuestion(
    title: String,
    question: QuestionUiState,
    options: List<String>,
    answer: String,
    modifier: Modifier,
    questionTitleSize: Int
) {
    var isExpanded by remember {
        mutableStateOf(false)
    }
    var textFieldValue by remember {
        mutableStateOf(answer)
    }


    Column(modifier = modifier) {
        Row(
            modifier = Modifier.padding(bottom = 8.dp)
        ) {
            Text(text = title, style = LocalTextStyle.current.copy(fontSize = questionTitleSize.sp))
        }

        Row {
        
            ExposedDropdownMenuBox(
                expanded = isExpanded,
                onExpandedChange = {
                isExpanded = it
            }) {

                OutlinedTextField(
                    value = textFieldValue,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded)
                    },
                    colors = ExposedDropdownMenuDefaults.textFieldColors(),
                    textStyle = TextStyle.Default.copy(fontSize = 13.sp),
                    modifier = Modifier
                        .menuAnchor()
                        .height(50.dp)
                )

            ExposedDropdownMenu(expanded = isExpanded,
                onDismissRequest = { isExpanded = false }
            ) {

                for (option in options) {
                    DropdownMenuItem(
                        text = {
                            Text(option)
                        },
                        onClick = {
                            isExpanded = false
                            textFieldValue = option
                            question.answer = option
                        })
                    }
                }
            }
         }
    }
}




