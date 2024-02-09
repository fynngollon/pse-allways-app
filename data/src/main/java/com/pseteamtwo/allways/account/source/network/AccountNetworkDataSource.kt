package com.pseteamtwo.allways.account.source.network

import com.pseteamtwo.allways.exception.ServerConnectionFailedException
import kotlin.jvm.Throws

interface AccountNetworkDataSource {
    @Throws(ServerConnectionFailedException::class)
    suspend fun loadAccount(email: String): NetworkAccount

    @Throws(ServerConnectionFailedException::class)
    suspend fun saveAccount(account: NetworkAccount)

    @Throws(ServerConnectionFailedException::class)
    suspend fun deleteAccount(account: NetworkAccount)

    @Throws(ServerConnectionFailedException::class)
    suspend fun doesEmailExist(email: String): Boolean

    @Throws(ServerConnectionFailedException::class)
    suspend fun doesPseudonymExist(pseudonym: String): Boolean
}