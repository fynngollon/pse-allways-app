package com.pseteamtwo.allways.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pseteamtwo.allways.data.account.repository.DefaultAccountRepository
import com.pseteamtwo.allways.data.exception.AccountAlreadyExistsException
import com.pseteamtwo.allways.data.exception.AccountNotFoundException
import com.pseteamtwo.allways.data.exception.InvalidEmailFormatException
import com.pseteamtwo.allways.data.exception.InvalidPasswordFormatException
import com.pseteamtwo.allways.data.exception.ServerConnectionFailedException
import com.pseteamtwo.allways.ui.login.LoginUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Viewmodel to retrieve and update the login related data for the [LoginScreen] and
 */
@HiltViewModel
class LoginViewModel @Inject constructor(private val accountRepository: DefaultAccountRepository): ViewModel() {
    private var _loginUiState: MutableStateFlow<LoginUiState> = MutableStateFlow(LoginUiState(loggedIn = false, loginFailed = false, serverConnectionFailed = false, accountAlreadyExists = false, invalidEmail = false, invalidPassword = false))
    val loginUiState: StateFlow<LoginUiState> = _loginUiState.asStateFlow()

    init {
        viewModelScope.launch {
        }
    }

    /**
     * funcction to set the loggedIn Boolean value of the [LoginUiState]
     * @param value the new value of the loggedIn Boolean
     */
    fun setLoggedIn(value: Boolean) {
        _loginUiState.value = LoginUiState(value, false, false, false, false, false)
    }


    /**
     * funcction to set the loginFailed Boolean value of the [LoginUiState]
     * @param value the new value of the loginFailed Boolean
     */
    fun setLoginFailed(value: Boolean) {
        _loginUiState.value = LoginUiState(false, value, false, false, false, false)
    }


    /**
     * funcction to set the serverConnectionFailed Boolean value of the [LoginUiState]
     * @param value the new value of the serverConnectionFailed Boolean
     */
    fun setServerConnectionFailed(value: Boolean) {
        _loginUiState.value = LoginUiState(false, false, value, false, false, false)
    }


    /**
     * funcction to set the accountAlreadyExists Boolean value of the [LoginUiState]
     * @param value the new value of the accountAlreadyExists Boolean
     */
    fun setAccountAlreadyExists(value: Boolean) {
        _loginUiState.value = LoginUiState(false, false, false, value, false, false)
    }

    /**
     * funcction to set the invalidEmail Boolean value of the [LoginUiState]
     * @param value the new value of the invalidEmail Boolean
     */
    fun setInvalidEmail(value: Boolean) {
        _loginUiState.value = LoginUiState(false, false, false, false,value,false)
    }

    /**
     * funcction to set the invalidPassword Boolean value of the [LoginUiState]
     * @param value the new value of the invalidPassword Boolean
     */
    fun setInvalidPassword(value: Boolean) {
        _loginUiState.value = LoginUiState(false, false, false, false, false, value)
    }

    /**
     * funcction to use the app without logging in with an account.
     */
    fun useWithoutAccount() {
        setLoggedIn(true)
    }


    /**
     * function to validate an account in a login attempt.
     * @param email the email address of the account
     * @param password the password of the account
     */
    fun validateLogin(email: String, password: String) {
        viewModelScope.launch {
            try {
                accountRepository.validateLogin(email, password)  //server is missing
                setLoggedIn(true)
            } catch (e: ServerConnectionFailedException) {
                setServerConnectionFailed(true)
            } catch (e: AccountNotFoundException) {
                setLoginFailed(true)
            }
        }
    }

    /**
     * function to crate an account .
     * @param email the email address of the account
     * @param password the password of the account
     */

    fun createAccount(email: String, password: String) {
        viewModelScope.launch {
            try {
                accountRepository.createAccount(email, password)
                setLoginFailed(true)
            } catch (e: ServerConnectionFailedException) {
                setServerConnectionFailed(true)
            } catch(e: AccountAlreadyExistsException) {
                setAccountAlreadyExists(true)
            } catch(e: InvalidEmailFormatException) {
                setInvalidEmail(true)
            } catch(e: InvalidPasswordFormatException) {
                setInvalidPassword(true)
            }
        }
    }
}