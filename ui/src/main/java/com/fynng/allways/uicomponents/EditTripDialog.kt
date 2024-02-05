package com.fynng.allways.uicomponents


import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.fynng.allways.trips.StageUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTripDialog(
    modifier: Modifier = Modifier,
    stageUiStates: List<StageUiState>
) {
    Card(modifier = modifier
        .fillMaxWidth()
        .padding(horizontal = 8.dp)) {
        LazyColumn(
            modifier = modifier
        ) {
            item { Text(
                text = "Weg bearbeiten",
                style = MaterialTheme.typography.titleLarge
            ) }
            items(stageUiStates) {
                stageUiState: StageUiState ->
                StageCard(
                    modifier = modifier,
                    stageUiState = stageUiState
                )
            }
        }
    }
}

@Preview
@Composable
fun EditTripDialogPreview() {
    val stageUiStates: List<StageUiState> = emptyList()
    EditTripDialog(
        modifier = Modifier,
        stageUiStates = stageUiStates
    )
}
