package com.pseteamtwo.allways.account.source.network

import kotlinx.coroutines.sync.Mutex

class DefaultAccountNetworkDataSource : AccountNetworkDataSource {
    private val accessMutex = Mutex()

    override suspend fun loadAccount(email: String): NetworkAccount {
        TODO("Not yet implemented")
    }

    override suspend fun saveAccount(account: NetworkAccount) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAccount(account: NetworkAccount) {
        TODO("Not yet implemented")
    }

    override suspend fun doesEmailExist(email: String): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun doesPseudonymExist(pseudonym: String): Boolean {
        TODO("Not yet implemented")
    }
}