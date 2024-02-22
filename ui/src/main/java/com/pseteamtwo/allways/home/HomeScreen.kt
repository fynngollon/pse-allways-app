package com.pseteamtwo.allways.home


import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.pseteamtwo.allways.profile.ProfileViewModel
import com.pseteamtwo.allways.uicomponents.DonateDataDialog

@Composable
fun HomeScreen(
    navController: NavController
) {
    var showDonateDataDialog by remember{ mutableStateOf(false) }
    val profileViewModel: ProfileViewModel = hiltViewModel()
    
    if (showDonateDataDialog) {
        DonateDataDialog(onDismiss = { showDonateDataDialog = false}, questions = profileViewModel.profileUiState.value.questions)
    }


    Button(
        modifier = Modifier.padding(top = 50.dp),
        onClick = {
            showDonateDataDialog = true
            }
        ) {
        Text(text = "Änderungen speichern")
    }
}