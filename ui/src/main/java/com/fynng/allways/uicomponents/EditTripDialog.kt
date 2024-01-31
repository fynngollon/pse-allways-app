package com.fynng.allways.uicomponents

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerLayoutType
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTripDialog(
    modifier: Modifier
) {
    Card(modifier = modifier
        .fillMaxWidth()
        .padding(horizontal = 8.dp)) {
        Box(modifier = modifier) {
            Text(text = "test")
        }
    }
}

@Preview
@Composable
fun EditTripDialogPreview() {
    EditTripDialog(modifier = Modifier)
}
