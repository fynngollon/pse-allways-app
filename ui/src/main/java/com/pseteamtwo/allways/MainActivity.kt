<<<<<<<<< Temporary merge branch 1:ui/src/main/java/com/fynng/allways/MainActivity.kt
package com.fynng.ui
=========
package com.pseteamtwo.allways
>>>>>>>>> Temporary merge branch 2:ui/src/main/java/com/pseteamtwo/allways/MainActivity.kt

import StageCard
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
<<<<<<<<< Temporary merge branch 1:ui/src/main/java/com/fynng/allways/MainActivity.kt
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
=========
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.pseteamtwo.allways.ui.theme.AllWaysTheme
import com.jakewharton.threetenabp.AndroidThreeTen
>>>>>>>>> Temporary merge branch 2:ui/src/main/java/com/pseteamtwo/allways/MainActivity.kt

// TODO
class MainActivity : ComponentActivity() {
    lateinit var navController: NavHostController

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO("either update to android 8 or use this for duration calculation")
        AndroidThreeTen.init(this)

        setContent {
            navController = rememberNavController()
            var showBottomBar by rememberSaveable { mutableStateOf(true) }
            val navBackStackEntry by navController.currentBackStackEntryAsState()

            showBottomBar = when (navBackStackEntry?.destination?.route) {
                "login_screen" -> false // on this screen bottom bar should be hidden
                 else -> true
            }

            Scaffold(
                bottomBar = {
                    BottomAppBar(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.primary,
                    ) {
                        if(showBottomBar)
                        {
                            BottomNavigation(navController = navController)
                        }
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

