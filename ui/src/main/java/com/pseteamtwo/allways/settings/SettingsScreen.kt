package com.pseteamtwo.allways.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun SettingsScreen() {
    Column {
        Row {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(text = "Tracking aktiviert")
            }
            Column(modifier = Modifier.padding(20.dp)) {
                TrackingCheckBox(answer = true)
            }
        }
    }
}

@Composable
fun TrackingCheckBox(answer: Boolean) {
    var checked: Boolean by remember {
        mutableStateOf(answer)
    }

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            modifier = Modifier
                .size(25.dp),
            checked = checked,
            onCheckedChange = {
                checked = it
                if(!checked) {

                }
            },
            colors = CheckboxDefaults.colors(
                uncheckedColor = Color.LightGray,
                checkedColor = Color.LightGray,
                checkmarkColor = Color.Magenta
            )
        )
    }
}
