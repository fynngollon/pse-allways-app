package com.pseteamtwo.allways.login

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable


@Composable
fun LoginScreen() {
    Column {
        Row {
            OutlinedTextField(value = "email", onValueChange = {})
        }
        Row {
            OutlinedTextField(value = "password", onValueChange = {})
        }
    }
}