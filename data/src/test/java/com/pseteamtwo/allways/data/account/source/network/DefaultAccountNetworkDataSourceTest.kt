package com.pseteamtwo.allways.data.account.source.network


import com.pseteamtwo.allways.data.account.source.network.AccountNetworkDataSource
import com.pseteamtwo.allways.data.account.source.network.DefaultAccountNetworkDataSource
import com.pseteamtwo.allways.data.account.source.network.NetworkAccount
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
            "richyrich",
            "123h142g4b1",
            "123uf1346"
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
    fun testSearchForEmail() {
        runBlocking {
            try {
                accountNetworkDataSource.doesEmailExist(networkAccount.pseudonym)
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