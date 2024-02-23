package com.pseteamtwo.allways.account.source.network

import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import java.sql.SQLException

class DefaultAccountNetworkDataSourceTest {

    private lateinit var accountNetworkDataSource: AccountNetworkDataSource
    private lateinit var networkAccount: NetworkAccount


    @Before
    fun setUp() {
        accountNetworkDataSource = DefaultAccountNetworkDataSource()
        networkAccount = NetworkAccount(
            "lol@gmail.com",
            "kdb",
            "kein plan",
            "was mach ich hier"
        )
    }

    @Test
    fun testSavingAccountToNetwork() {
        runBlocking {
            try {
                accountNetworkDataSource.saveAccount(networkAccount)
            } catch (e: SQLException) {
                assert(false){ "SQL Exception" }
            }
        }
    }

    @Test
    fun testSearchForPseudonym() {
        runBlocking {
            try {
                accountNetworkDataSource.doesPseudonymExist(networkAccount.pseudonym)
            } catch (e: SQLException) {
                assert(false){ "SQL Exception" }
            }
        }
    }

    @Test
    fun testLoadAccount() {
        runBlocking {
            try {
                accountNetworkDataSource.loadAccount(networkAccount.email)
            } catch (e: SQLException) {
                assert(false){ "SQL Exception" }
            }
        }
    }

    @Test
    fun testDeletingAccountFromNetwork() {
        runBlocking {
            try {
                accountNetworkDataSource.deleteAccount(networkAccount)
            } catch (e: SQLException) {
                assert(false){ "SQL Exception" }
            }
        }
    }
}