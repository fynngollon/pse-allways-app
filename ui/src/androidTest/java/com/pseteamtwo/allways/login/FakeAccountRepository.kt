package com.pseteamtwo.allways.login

import com.pseteamtwo.allways.account.Account
import com.pseteamtwo.allways.account.repository.AccountRepository
import kotlinx.coroutines.flow.Flow

class FakeAccountRepository : AccountRepository{
    override fun observe(): Flow<Account> {
        TODO("Not yet implemented")
    }

    override suspend fun createAccount(email: String, password: String) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAccount() {
        TODO("Not yet implemented")
    }

    override suspend fun validateLogin(email: String, password: String): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun authenticateAccount(): Boolean {
        TODO("Not yet implemented")
    }
}