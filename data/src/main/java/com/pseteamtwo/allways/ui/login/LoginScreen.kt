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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.pseteamtwo.allways.R
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
                        Text(text = stringResource(id = R.string.no_connection_to_server))

                    }
                    Row(modifier = Modifier.padding(20.dp)) {
                        Button(onClick = onDismissRequest) {
                            Text(text = stringResource(id = R.string.ok))
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
                        Text(text = stringResource(id = R.string.login_failed))
                    }
                    Row(modifier = Modifier.padding(20.dp)) {
                        Button(onClick =  onDismissRequest ) {
                            Text(text = stringResource(id = R.string.ok))
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
                        Text(text = stringResource(id = R.string.account_already_exists))

                    }
                    Row(modifier = Modifier.padding(20.dp)) {
                        Button(onClick =  onDismissRequest) {
                            Text(text = stringResource(id = R.string.ok))
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
                        Text(text =  stringResource(id = R.string.email_invalid_format))
                    }
                    Row(modifier = Modifier.padding(20.dp)) {
                        Button(onClick =  onDismissRequest) {
                            Text(text = stringResource(id = R.string.ok))
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
                        Text(text =  stringResource(id = R.string.password_invalid_format))
                    }
                    Row(modifier = Modifier.padding(20.dp)) {
                        Button(onClick =  onDismissRequest) {
                            Text(text = stringResource(id = R.string.ok))
                        }
                    }
                }
            }
        }
    }


    
    

    Column(modifier = Modifier.padding(20.dp)) {
        Row(modifier = Modifier.padding(start = 20.dp)) {
            Text(text = stringResource(id = R.string.email))
        }
        Row (modifier = Modifier.padding( start = 20.dp)){
            OutlinedTextField(value = email, onValueChange = {it -> email = it})
        }

        Row (modifier = Modifier.padding(top = 20.dp, start = 20.dp)){
            Text(text = stringResource(id = R.string.password))
        }
        Row(modifier = Modifier.padding(start = 20.dp)) {
            OutlinedTextField(value = password, onValueChange = {it -> password = it})
        }

        Row(modifier = Modifier.padding(start = 20.dp, top = 15.dp)) {
            Column {
                Button(onClick = {loginViewModel.validateLogin(email, password)}) {
                    Text(text = stringResource(id = R.string.login_text))
                }
            }
            Column(modifier = Modifier.padding(start = 20.dp)) {
                Button(onClick = {loginViewModel.createAccount(email, password)}) {
                    Text(text = stringResource(id = R.string.register))
                }
            }

        }

        Row (modifier = Modifier.padding(start = 20.dp, top = 15.dp)){
            Button(onClick = {loginViewModel.useWithoutAccount()}) {
                Text(text = stringResource(id = R.string.use_without_account))
            }
        }

    }
}
