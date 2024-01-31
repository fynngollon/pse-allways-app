package com.pseteamtwo.allways.account.repository

import com.pseteamtwo.allways.exception.AccountAlreadyExistsException
import com.pseteamtwo.allways.exception.ServerConnectionFailedException
import com.pseteamtwo.allways.account.Account
import kotlinx.coroutines.flow.Flow
import kotlin.jvm.Throws

interface AccountRepository {

    fun observe(): Flow<Account>

    @Throws(ServerConnectionFailedException::class, AccountAlreadyExistsException::class)
    suspend fun createAccount(email: String, password: String)

    @Throws(ServerConnectionFailedException::class)
    suspend fun deleteAccount()

    @Throws(ServerConnectionFailedException::class)
    suspend fun validateLogin(email: String, password: String): Boolean

    @Throws(ServerConnectionFailedException::class)
    suspend fun authenticateAccount(): Boolean
}