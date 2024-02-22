package com.fynng.allways

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.fynng.allways.navigation.BottomNavigation
import com.fynng.allways.navigation.SetUpNavGraph

import com.fynng.allways.ui.theme.AllWaysTheme
import com.jakewharton.threetenabp.AndroidThreeTen


// TODO
class MainActivity : ComponentActivity() {
    lateinit var navController: NavHostController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO("either update to android 8 or use this for duration calculation")
        AndroidThreeTen.init(this)

        setContent {
            AllWaysTheme {

                /*val testTripAndStageRepository = TestTripAndStageRepository()
                val tripsViewModel = TripsViewModel(testTripAndStageRepository)
                //var tripUiState = tripsViewModel.getTripUiState(1)

                TripsScreen(tripsViewModel = tripsViewModel)*/
                navController = rememberNavController()
                //StageCard()

                Scaffold(
                    bottomBar = {
                        BottomAppBar(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.primary,
                        ) {
                            BottomNavigation(navController = navController)
                        }
                    },

                    ) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .padding(innerPadding)
                    ) {
                        SetUpNavGraph(navController = navController)
                    }
                }

            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AllWaysTheme {
        Greeting("Android")
    }
}