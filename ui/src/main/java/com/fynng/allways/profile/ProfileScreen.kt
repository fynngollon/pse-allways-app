package com.fynng.allways.profile


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController


@Composable
fun ProfileScreen(
    navController: NavController,

    ) {
    Column {
        Row (){
            Text(
                text = "Profil",
                fontSize = 30.sp,
                modifier = Modifier
                    .padding(start = 20.dp)
            )
        }
    }
}

@Composable
fun QuestionItem() {

}

@Composable
fun TextQuestion(title: String, answer: String) {

}

@Composable
fun RadioButtonQuestion(title: String, options: List<String>, answer: String) {}