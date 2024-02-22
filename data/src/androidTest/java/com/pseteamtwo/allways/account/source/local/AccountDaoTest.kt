package com.pseteamtwo.allways.account.source.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * This test tests [com.pseteamtwo.allways.account.source.local.AccountDao]
 * and [com.pseteamtwo.allways.account.source.local.AccountDatabase].
 */
@RunWith(AndroidJUnit4::class)
class AccountDaoTest {
    // using an in-memory database because the information stored here disappears when the
    // process is killed
    private lateinit var accountDatabase: AccountDatabase


    // Ensure that we use a new database for each test.
    @Before
    fun initDb() {
        accountDatabase = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AccountDatabase::class.java
        ).allowMainThreadQueries().build()
    }


    @Test
    fun insertAccountAndGetBack() = runTest {
        // GIVEN - insert an account
        val account = LocalAccount(
            "email",
            "pseudonym",
            "passwordHash",
            "passwordSalt"
        )
        accountDatabase.accountDao().upsert(account)

        // WHEN - Get the account by id from the database
        val loaded = accountDatabase.accountDao().observe().first()

        // THEN - The loaded data contains the expected values
        assertNotNull(loaded as LocalAccount)
        assertEquals(account.email, loaded.email)
        assertEquals(account.pseudonym, loaded.pseudonym)
        assertEquals(account.passwordHash, loaded.passwordHash)
        assertEquals(account.passwordSalt, loaded.passwordSalt)
    }

    @Test
    fun insertTaskDeleteTaskIsEmpty() = runTest {
        accountDatabase.accountDao().upsert(
            LocalAccount(
                "email",
                "pseudonym",
                "passwordHash",
                "passwordSalt"
            )
        )

        assertEquals(1, accountDatabase.accountDao().deleteAccount())

        try {
            accountDatabase.accountDao().observe().first()
        } catch (e: NoSuchElementException) {
            assert(true)
        }
    }
}