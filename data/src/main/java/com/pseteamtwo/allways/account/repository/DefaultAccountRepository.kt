package com.pseteamtwo.allways.account.repository

import com.pseteamtwo.allways.account.Account
import com.pseteamtwo.allways.account.source.local.AccountDao
import com.pseteamtwo.allways.account.source.local.LocalAccount
import com.pseteamtwo.allways.account.source.network.AccountNetworkDataSource
import com.pseteamtwo.allways.account.toExternal
import com.pseteamtwo.allways.account.toNetwork
import com.pseteamtwo.allways.di.ApplicationScope
import com.pseteamtwo.allways.di.DefaultDispatcher
import com.pseteamtwo.allways.exception.AccountAlreadyExistsException
import com.pseteamtwo.allways.exception.ServerConnectionFailedException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.security.MessageDigest
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.jvm.Throws

@Singleton
class DefaultAccountRepository @Inject constructor(
    private val accountLocalDataSource: AccountDao,
    private val accountNetworkDataSource: AccountNetworkDataSource,
    @DefaultDispatcher private val dispatcher: CoroutineDispatcher,
    @ApplicationScope private val scope: CoroutineScope,
) : AccountRepository {

    override fun observe(): Flow<Account> {
        return accountLocalDataSource.observe().map { it.toExternal() }
    }

    override suspend fun createAccount(email: String, password: String) {
        // checks if the user is already logged in to an account
        if (accountLocalDataSource.observe().count() > 0) {
            // TODO("user is already logged in, is this case even possible")
            return
        }

        // checks if email exists in network database
        if (accountNetworkDataSource.doesEmailExist(email)) {
            throw AccountAlreadyExistsException()
        }

        // creates the pseudonym for the account
        // checks if pseudonym exists in network database
        // creates a new one until it is unique
        var pseudonym: String
        var offset: String = ""
        do {
            pseudonym = withContext(dispatcher) {
                createPseudonym(email + offset)
            }
            offset += email
        } while (accountNetworkDataSource.doesPseudonymExist(pseudonym))

        // generates a random string as password salt
        val salt = withContext(dispatcher) {
            UUID.randomUUID().toString() // TODO("is UUID correct for this or Random?")
        }

        // encrypts the password
        val passwordHash = withContext(dispatcher) {
            hashPassword(password, salt)
        }

        val localAccount = LocalAccount(
            email = email,
            pseudonym = pseudonym,
            passwordHash = passwordHash,
            passwordSalt = salt
        )

        accountNetworkDataSource.saveAccount(localAccount.toNetwork())
        accountLocalDataSource.upsert(localAccount)
    }

    override suspend fun deleteAccount() {
        if (!authenticateAccount()) {
            // TODO("What if it cant be verified")
            return
        }

        accountNetworkDataSource.deleteAccount(accountLocalDataSource.observe().first().toNetwork())
        accountLocalDataSource.deleteAccount()
    }

    override suspend fun validateLogin(email: String, password: String): Boolean {
        // checks if an account exists with the email
        if (!accountNetworkDataSource.doesEmailExist(email)) {
            return false
        }

        // compares the passwords
        val networkAccount = accountNetworkDataSource.loadAccount(email)
        val passwordHash = withContext(dispatcher) {
            hashPassword(password, networkAccount.passwordSalt)
        }

        return passwordHash == networkAccount.passwordHash
    }

    // doesn't compare email rn
    // TODO do we need validate and authenticate?
    // TODO needs review
    override suspend fun authenticateAccount(): Boolean {
        // loads the local account
        val localAccount = accountLocalDataSource.observe().first()

        // checks if account exists on network database and deletes the local account if it doesn't
        if (!accountNetworkDataSource.doesPseudonymExist(localAccount.pseudonym)) {
            accountLocalDataSource.deleteAccount()
            return false
        }

        // loads the network account
        val networkAccount = accountNetworkDataSource.loadAccount(localAccount.pseudonym)

        return localAccount.passwordHash == networkAccount.passwordHash
                && localAccount.passwordSalt == networkAccount.passwordSalt
    }

    private fun hashPassword(password: String, salt: String): String {
        val md = MessageDigest.getInstance("SHA-256")
        md.update(salt.toByteArray())
        val hashedPassword = md.digest(password.toByteArray())
        return hashedPassword.toString()
    }

    private fun createPseudonym(email: String): String {
        val md = MessageDigest.getInstance("SHA-256")
        val hashedEmail = md.digest(email.toByteArray())
        return hashedEmail.toString()
    }

}