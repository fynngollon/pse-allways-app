package com.pseteamtwo.allways.data.account.repository

import com.pseteamtwo.allways.data.account.Account
import com.pseteamtwo.allways.data.account.source.local.AccountDao
import com.pseteamtwo.allways.data.account.source.local.LocalAccount
import com.pseteamtwo.allways.data.account.source.network.AccountNetworkDataSource
import com.pseteamtwo.allways.data.account.toExternal
import com.pseteamtwo.allways.data.account.toNetwork
import com.pseteamtwo.allways.data.di.DefaultDispatcher
import com.pseteamtwo.allways.data.exception.AccountAlreadyExistsException
import com.pseteamtwo.allways.data.exception.AccountNotFoundException
import com.pseteamtwo.allways.data.exception.InvalidEmailFormatException
import com.pseteamtwo.allways.data.exception.InvalidPasswordFormatException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.security.MessageDigest
import java.util.UUID
import java.util.regex.Matcher
import java.util.regex.Pattern
import javax.inject.Inject
import javax.inject.Singleton

/**
 * This implementation of [AccountRepository] holds a local and a network data access object
 * to access the stored account in the local database and to compare the account logged in to to
 * all known accounts on the network account database.
 * This class follows the singleton-pattern.
 *
 * @property accountLocalDataSource A [AccountDao] to access the local account database.
 * @property accountNetworkDataSource A [AccountNetworkDataSource] to access the network
 * account database.
 * @property dispatcher A dispatcher to allow asynchronous function calls because this class uses
 * complex computing and many accesses to databases which shall not block the program flow.
 * @constructor Creates an instance of this class.
 */
@Singleton
class DefaultAccountRepository @Inject constructor(
    private val accountLocalDataSource: AccountDao,
    private val accountNetworkDataSource: AccountNetworkDataSource,
    @DefaultDispatcher private val dispatcher: CoroutineDispatcher,
    //@ApplicationScope private val scope: CoroutineScope,
) : AccountRepository {

    companion object{
        private val EMAIL_REGEX: Pattern =
            Pattern.compile(
                "^[A-Z0-9. _-]+@[A-Z0-9. -]+\\.[A-Z]{2,4}$",
                Pattern.CASE_INSENSITIVE
            )
        //Minimum four characters, at least one uppercase letter, one lowercase letter, one
        //number and one special character:
        private val PASSWORD_REGEX: Pattern =
            Pattern.compile(
                "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&.])[A-Za-z\\d@$!%*?&.]{4,}$"
            )
    }

    override fun observe(): Flow<Account> {
        return accountLocalDataSource.observe().map { it.toExternal() }
    }

    override suspend fun createAccount(email: String, password: String) {
        // checks if the user is already logged in to an account
        assert (accountLocalDataSource.getAll().isEmpty()) {
            "Another account is already saved in database."
        }

        //checks if the given email satisfies the [EMAIL_REGEX] pattern
        val emailMatcher: Matcher = EMAIL_REGEX.matcher(email)
        if(!emailMatcher.matches()) {
            throw InvalidEmailFormatException("The given email is of invalid format.")
        }
        //checks if the given password satisfies the [PASSWORD_REGEX] pattern
        val passwordMatcher: Matcher = PASSWORD_REGEX.matcher(password)
        if(!passwordMatcher.matches()) {
            throw InvalidPasswordFormatException(
                "The given password has to contain at least\n" +
                        "one uppercase letter,\n" +
                        "one lowercase letter,\n" +
                        "one number,\n" +
                        "one special character."
            )
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
            UUID.randomUUID().toString()
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
            throw AccountNotFoundException()
        }

        accountNetworkDataSource.deleteAccount(accountLocalDataSource.observe().first().toNetwork())
        accountLocalDataSource.deleteAccount()
    }


    override suspend fun validateLogin(email: String, password: String): Boolean {
        // checks if an account exists with the email
        if (!accountNetworkDataSource.doesEmailExist(email)) {
            throw AccountNotFoundException()
        }

        // compares the passwords
        val networkAccount = accountNetworkDataSource.loadAccount(email)
        val passwordHash = withContext(dispatcher) {
            hashPassword(password, networkAccount.passwordSalt)
        }

        return passwordHash == networkAccount.passwordHash
    }


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