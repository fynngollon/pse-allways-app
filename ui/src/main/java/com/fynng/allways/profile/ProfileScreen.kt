package com.fynng.allways.profile


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
import androidx.navigation.NavController
import com.pseteamtwo.allways.question.QuestionType


@Composable
fun ProfileScreen(
    navController: NavController,
    profileViewModel: ProfileViewModel
    ) {



    Column(
        modifier = Modifier
            .padding(start = 20.dp, top = 15.dp)
    ) {
        val profile by profileViewModel.profileUiState.collectAsState()
        val questions = profile.questions
        Row {


            LazyColumn(
                modifier = Modifier.fillMaxWidth()
            ) {
                for (question in questions) {
                    item {
                        Row(
                            modifier = Modifier
                                .padding(top = 10.dp)
                        ) {
                            when (question.type) {
                                QuestionType.TEXT -> TextQuestion(
                                    title = question.title,
                                    answer = question.answer
                                )

                                QuestionType.CHECKBOX -> CheckBoxQuestion(

                                    title = question.title,
                                    options = question.options,
                                    answer = question.answer
                                )

                                QuestionType.RADIO_BUTTON -> RadioButtonQuestion(
                                    title = question.title,
                                    options = question.options,
                                    answer = question.answer
                                )

                                QuestionType.SPINNER -> SpinnerQuestion(
                                    title = question.title,
                                    options = question.options,
                                    answer = question.answer
                                )

                                else -> {}
                            }
                        }
                    }
                }
            }
        }
        Row(
            modifier = Modifier.fillMaxHeight(),
            verticalAlignment = Alignment.Bottom
        ) {
            Box() {
                Button(
                    modifier = Modifier.padding(top = 50.dp),
                    onClick = {
                        for (question in questions) {
                            profileViewModel.updateAnswer(question.id, "answer")
                        }
                    }) {
                    Text(text = "Änderungen speichern")
                }
            }
        }

    }
}


@Composable
fun QuestionItem() {

}

@Composable
fun TextQuestion(title: String, answer: String) {
    var text by remember { mutableStateOf(answer) }
    TextField(
        value = text,
        onValueChange = {
            text = it
        },
        label = {
            Text(title)
        }
    )
}

@Composable
fun RadioButtonQuestion(title: String, options: List<String>, answer: String) {

}

@Composable
fun CheckBoxQuestion(title: String, options: List<String>, answer: String) {
    Column {
        Text(text = title, style = LocalTextStyle.current.copy(fontSize = 20.sp))
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
                uncheckedColor = Color.Green,
                checkedColor = Color.Yellow,
                checkmarkColor = Color.Magenta
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpinnerQuestion(title: String, options: List<String>, answer: String) {
    var isExpanded by remember {
        mutableStateOf(false)
    }
    var textFieldValue by remember {
        mutableStateOf(answer)
    }
    ExposedDropdownMenuBox(
        expanded = isExpanded,
        onExpandedChange = {isExpanded = it
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
                    })
            }
        }
    }
}




