package com.pseteamtwo.allways.login

import com.pseteamtwo.allways.account.repository.DefaultAccountRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val accountRepository: DefaultAccountRepository) {


    fun validateLogin(email: String, password: String) {
        
    }
}