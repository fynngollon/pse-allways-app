package com.pseteamtwo.allways.login

data class LoginUiState(
    var loggedIn: Boolean,
    var loginFailed: Boolean,
    var serverConnectionFailed: Boolean,
    var accountAlreadyExists: Boolean
)
