package com.pseteamtwo.allways.ui.login

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)

class LoginViewModelTest {

    private lateinit var fakeAccountRepository: FakeAccountRepository
    private lateinit var viewModel: LoginViewModel

    @Before
    fun setUp() {
        fakeAccountRepository = FakeAccountRepository()
        viewModel = LoginViewModel(fakeAccountRepository)
    }

    @Test
    fun testSetLoggedIn() {
        viewModel.setLoggedIn(true)
        assert(viewModel.loginUiState.value.loggedIn)
    }

    @Test
    fun testSetLoginFailed() {
        viewModel.setLoginFailed(true)
        assert(viewModel.loginUiState.value.loginFailed)
    }

    @Test
    fun setServerConnectionFailed() {
        viewModel.setServerConnectionFailed(true)
        assert(viewModel.loginUiState.value.serverConnectionFailed)
    }

    @Test
    fun setAccountAlreadyExists() {
        viewModel.setAccountAlreadyExists(true)
        assert(viewModel.loginUiState.value.accountAlreadyExists)
    }

    @Test
    fun setInvalidEmail() {
        viewModel.setInvalidEmail(true)
        assert(viewModel.loginUiState.value.invalidEmail)
    }

    @Test
    fun setInvalidPassword() {
        viewModel.setInvalidPassword(true)
        assert(viewModel.loginUiState.value.invalidPassword)
    }

    @Test
    fun setUseWithoutAccount() {
        viewModel.setLoggedIn(true)
        assert(viewModel.loginUiState.value.loggedIn)
    }

}