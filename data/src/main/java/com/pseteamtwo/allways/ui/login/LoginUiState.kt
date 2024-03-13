package com.pseteamtwo.allways.ui.login

/**
 *Representation of the information displayed on the [LoginScreen]
 *
 */

data class LoginUiState(
    /**
     * Boolean value showing if user is logged in or not.
     */
    var loggedIn: Boolean,

    /**
     * Boolean value showing if the login attempt has failed.
     */
    var loginFailed: Boolean,

    /**
     * Boolean value showing if the connection to the server has failed.
     */
    var serverConnectionFailed: Boolean,

    /**
     * Boolean value showing if the account that was attempted to create already exists
     */
    var accountAlreadyExists: Boolean
)
