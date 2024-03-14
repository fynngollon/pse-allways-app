package com.pseteamtwo.allways.data.account.repository


import android.util.Log
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.pseteamtwo.allways.data.account.source.local.AccountDao
import com.pseteamtwo.allways.data.account.source.local.AccountDatabase
import com.pseteamtwo.allways.data.account.source.network.AccountNetworkDataSource
import com.pseteamtwo.allways.data.account.source.network.DefaultAccountNetworkDataSource
import com.pseteamtwo.allways.data.exception.AccountAlreadyExistsException
import com.pseteamtwo.allways.data.exception.AccountNotFoundException
import com.pseteamtwo.allways.data.exception.InvalidEmailFormatException
import com.pseteamtwo.allways.data.exception.InvalidPasswordFormatException
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

/**
 * Instrumented test, which will execute on an Android device.
 * These tests require an established connection to the network database in order to ensure right
 * testing because otherwise all tests will likely fail
 * because of [com.pseteamtwo.allways.exception.ServerConnectionFailedException].
 *
 * This test tests [com.pseteamtwo.allways.data.account.repository.DefaultAccountRepository].
 */
class DefaultAccountRepositoryTest {

    //Test data
    private val email = "killua.zoldyck@hxh.com"
    private val invalidEmail = "killua.zoldyck@hxh"
    private val password = "Godspeed!99"
    private val password2 = "Nanika?99Alluka!"
    private val invalidPassword = "Godspeed"


    //Test dependencies
    private lateinit var database: AccountDatabase
    private lateinit var accountDao: AccountDao
    private lateinit var accountNetworkDataSource: AccountNetworkDataSource

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
        accountNetworkDataSource = DefaultAccountNetworkDataSource()

        repository = DefaultAccountRepository(
            accountDao,
            accountNetworkDataSource,
            testDispatcher,
            //testScope
        )

    }


    @Test
    fun createAccountAndGetBack() = runTest {
        repository.createAccount(email, password)

        val createdAccount = repository.observe().first()
        assertNotNull(accountDao.observe().first())
        assertEquals(true, accountNetworkDataSource.doesEmailExist(email))
        assertEquals(email, createdAccount.email)
        Log.d("Created Account", createdAccount.toString())
    }

    @Test(expected = InvalidEmailFormatException::class)
    fun createAccountWithInvalidEmailFormat() = runTest {
        repository.createAccount(invalidEmail, password)
    }

    @Test(expected = InvalidPasswordFormatException::class)
    fun createAccountWithInvalidPasswordFormat() = runTest {
        repository.createAccount(email, invalidPassword)
    }

    @Test(expected = AccountAlreadyExistsException::class)
    fun createAccountWithSameEmailAlreadyOnDatabase() = runTest {
        repository.createAccount(email, password)
        //Only delete from local database to force exception on creating another account with
        //the same email
        accountDao.deleteAccount()
        repository.createAccount(email, password2)
    }

    @Test
    fun deleteAccount() = runTest {
        //Needs an account already inside the local and network database
        repository.createAccount(email, password)
        //Delete the account from the local and network database
        repository.deleteAccount()

        //Ensure that the account was deleted from local and network database
        assertEquals(null, repository.observe().first())
        assertEquals(false, accountNetworkDataSource.doesEmailExist(email))
    }

    @Test(expected = AccountNotFoundException::class)
    fun validateLoginWithoutAccordingAccountOnNetworkDatabase() = runTest {
        assertEquals(false, accountNetworkDataSource.doesEmailExist(email))
        repository.validateLogin(email, password)
    }

    @Test
    fun validateLogin() = runTest {
        //Needs an account already inside the network database, but not on local database
        repository.createAccount(email, password)
        accountDao.deleteAccount()

        assertEquals(true, repository.validateLogin(email, password))
    }
}