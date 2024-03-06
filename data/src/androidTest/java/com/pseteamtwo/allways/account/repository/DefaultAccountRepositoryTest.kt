package com.pseteamtwo.allways.account.repository

import android.util.Log
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.pseteamtwo.allways.account.source.local.AccountDao
import com.pseteamtwo.allways.account.source.local.AccountDatabase
import com.pseteamtwo.allways.account.source.network.DefaultAccountNetworkDataSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

/**
 * Instrumented test, which will execute on an Android device.
 *
 * This test tests [com.pseteamtwo.allways.account.repository.DefaultAccountRepository].
 */
class DefaultAccountRepositoryTest {

    //Test data
    private val email = "killua.zoldyck@hxh.com"
    private val password = "Godspeed99"


    //Test dependencies
    private lateinit var database: AccountDatabase
    private lateinit var accountDao: AccountDao

    @OptIn(ExperimentalCoroutinesApi::class)
    private var testDispatcher = UnconfinedTestDispatcher()
    private var testScope = TestScope(testDispatcher)

    //Class under test
    private lateinit var repository: DefaultAccountRepository



    // using an in-memory database because the information stored here disappears when the
    // process is killed
    // Ensure that we use a new database for each test.
    @Before
    fun createRepository() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AccountDatabase::class.java
        ).allowMainThreadQueries().build()

        accountDao = database.accountDao()

        repository = DefaultAccountRepository(
            accountDao,
            DefaultAccountNetworkDataSource(),
            testDispatcher,
            //testScope
        )

    }


    @Test
    fun createAccountAndGetBack() = runTest {
        repository.createAccount(email, password)

        val createdAccount = repository.observe().first()
        Log.d("Created Account", createdAccount.toString())
    }

    @Test
    fun deleteAccount() {
        TODO("Not yet implemented")
    }

    @Test
    fun validateLogin() {
        TODO("Not yet implemented")
    }

    @Test
    fun authenticateAccount() {
        TODO("Not yet implemented")
    }
}