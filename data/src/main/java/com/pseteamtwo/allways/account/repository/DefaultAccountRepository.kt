package com.pseteamtwo.allways.account.repository

import com.pseteamtwo.allways.account.Account
import kotlinx.coroutines.flow.Flow

class DefaultAccountRepository : AccountRepository {
    override fun observe(): Flow<Account> {
        TODO("Not yet implemented")
    }

    override suspend fun createAccount(email: String, password: String) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAccount() {
        TODO("Not yet implemented")
    }

    override suspend fun validateAccount(pseudonym: String, password: String): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun authenticateAccount(pseudonym: String, passwordHash: String): Boolean {
        TODO("Not yet implemented")
    }
}