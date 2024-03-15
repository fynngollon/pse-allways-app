package com.pseteamtwo.allways.ui.login

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.pseteamtwo.allways.ui.navigation.Screen

/**
 * Composable function for displaying all the items in the login screen
 */
@Composable
fun LoginScreen(navController: NavController) {
    val loginViewModel: LoginViewModel = hiltViewModel()
    val login by loginViewModel.loginUiState.collectAsState()
    var loggedIn = login.loggedIn
    var loginFailed = login.loginFailed
    var serverConnectionFailed = login.serverConnectionFailed
    var accountAlreadyExists = login.accountAlreadyExists
    var invalidEmail = login.invalidEmail
    var invalidPassword = login.invalidPassword
    var email by remember{mutableStateOf("")}
    var password by remember {
        mutableStateOf("")
    }

    if(loggedIn) {
        navController.navigate(Screen.Home.route)
    }
    
    if(serverConnectionFailed) {
        val onDismissRequest = {loginViewModel.setServerConnectionFailed(false)}
        Dialog(onDismissRequest = onDismissRequest) {
            Card {
                Column {
                    Row(modifier = Modifier.padding(20.dp)) {
                        Text(text = "Derzeit ist keine Verbindung zum Server herstellbar. Versuchen Sie es zu einem späteren Zeitpunkt noch einmal.")

                    }
                    Row(modifier = Modifier.padding(20.dp)) {
                        Button(onClick = onDismissRequest) {
                            Text(text = "OK")
                        }
                    }
                }

            }
        }
    }
    
    if(loginFailed) {
        val onDismissRequest = {loginViewModel.setLoginFailed(false)}
        Dialog(onDismissRequest = onDismissRequest) {
            Card {
                Column {
                    Row(modifier = Modifier.padding(20.dp)) {
                        Text(text = "Account konnte nicht eingeloggt werden")
                    }
                    Row(modifier = Modifier.padding(20.dp)) {
                        Button(onClick =  onDismissRequest ) {
                            Text(text = "OK")
                        }
                    }
                }

            }
        }
    }

    if(accountAlreadyExists) {
        val onDismissRequest = {loginViewModel.setAccountAlreadyExists(false)}
        Dialog(onDismissRequest = onDismissRequest) {
            Card {
                Column {
                    Row(modifier = Modifier.padding(20.dp)) {
                        Text(text = "Account mit diesem Namen existiert bereits.")

                    }
                    Row(modifier = Modifier.padding(20.dp)) {
                        Button(onClick =  onDismissRequest) {
                            Text(text = "OK")
                        }
                    }
                }

            }
        }
    }


    if(invalidEmail) {
        val onDismissRequest = {loginViewModel.setAccountAlreadyExists(false)}
        Dialog(onDismissRequest = onDismissRequest) {
            Card {
                Column {
                    Row(modifier = Modifier.padding(20.dp)) {
                        Text(text =  "The given password has to contain at least\n" +
                                "one uppercase letter,\n" +
                                "one lowercase letter,\n" +
                                "one number,\n" +
                                "one special character.")
                    }
                    Row(modifier = Modifier.padding(20.dp)) {
                        Button(onClick =  onDismissRequest) {
                            Text(text = "OK")
                        }
                    }
                }
            }
        }
    }


    if(invalidPassword) {
        val onDismissRequest = {loginViewModel.setAccountAlreadyExists(false)}
        Dialog(onDismissRequest = onDismissRequest) {
            Card {
                Column {
                    Row(modifier = Modifier.padding(20.dp)) {
                        Text(text =  "The given email is of invalid format.")
                    }
                    Row(modifier = Modifier.padding(20.dp)) {
                        Button(onClick =  onDismissRequest) {
                            Text(text = "OK")
                        }
                    }
                }
            }
        }
    }


    
    

    Column(modifier = Modifier.padding(20.dp)) {
        Row(modifier = Modifier.padding(start = 20.dp)) {
            Text(text = "Email")
        }
        Row (modifier = Modifier.padding( start = 20.dp)){
            OutlinedTextField(value = email, onValueChange = {it -> email = it})
        }

        Row (modifier = Modifier.padding(top = 20.dp, start = 20.dp)){
            Text(text = "Passwort")
        }
        Row(modifier = Modifier.padding(start = 20.dp)) {
            OutlinedTextField(value = password, onValueChange = {it -> password = it})
        }

        Row(modifier = Modifier.padding(start = 20.dp, top = 15.dp)) {
            Column {
                Button(onClick = {loginViewModel.validateLogin(email, password)}) {
                    Text(text = "Einloggen")
                }
            }
            Column(modifier = Modifier.padding(start = 20.dp)) {
                Button(onClick = {loginViewModel.createAccount(email, password)}) {
                    Text(text = "Registrieren")
                }
            }

        }

        Row (modifier = Modifier.padding(start = 20.dp, top = 15.dp)){
            Button(onClick = {loginViewModel.useWithoutAccount()}) {
                Text(text = "Ohne Account benutzen")
            }
        }

    }
}
