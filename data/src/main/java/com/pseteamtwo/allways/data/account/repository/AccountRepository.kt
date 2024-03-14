package com.pseteamtwo.allways.data.account.repository

import com.pseteamtwo.allways.data.account.Account
import com.pseteamtwo.allways.data.exception.AccountAlreadyExistsException
import com.pseteamtwo.allways.data.exception.AccountNotFoundException
import com.pseteamtwo.allways.data.exception.ServerConnectionFailedException
import com.pseteamtwo.allways.data.exception.InvalidEmailFormatException
import kotlinx.coroutines.flow.Flow
import kotlin.jvm.Throws

/**
 * Repository to handle the user [Account] and to provide according functionality for
 * the ui-layer.
 *
 * This repository provides functionality to retrieve from and save to local and network account
 * databases and synchronize them as well as to create and delete an [Account] or validate
 * a login request from the user.
 */
interface AccountRepository {

    /**
     * Retrieves the [Account] saved in the local account database.
     *
     * @return A flow of the saved [Account] the user is logged into at the moment.
     */
    fun observe(): Flow<Account>

    /**
     * Creates a new [Account] with the provided parameters.
     * Therefore creates a pseudonym out of the given email and saves the created [Account] into
     * the local account database.
     *
     * @param email The email to login to the account after logout.
     * @param password The password to login to the account later after logout.
     * @throws ServerConnectionFailedException If no connection to the network database can be
     * established.
     * @throws InvalidEmailFormatException If the email specified by the user is of invalid format.
     * @throws AccountAlreadyExistsException If an [Account] with the given email already exists
     * on the network database.
     */
    @Throws(
        ServerConnectionFailedException::class,
        InvalidEmailFormatException::class,
        AccountAlreadyExistsException::class
    )
    suspend fun createAccount(email: String, password: String)

    /**
     * Deletes the [Account] which the user in this application is currently logged in to
     * out of the local and network database.
     * With the [Account] being deleted from the network account database, every information
     * on the network database linked to this account (questions, trips and stages) should also
     * be deleted.
     *
     * @throws ServerConnectionFailedException If no connection to the network database can be
     * established.
     * @throws AccountNotFoundException If the user in this application is not logged in to an
     * [Account] already and thus no [Account] is saved in the
     * local database at the moment to be deleted.
     */
    @Throws(ServerConnectionFailedException::class, AccountNotFoundException::class)
    suspend fun deleteAccount()

    /**
     * Validates the login request of the user by checking if the specified password is the same
     * as given on the creation of the account with this email.
     *
     * @param email The email to login to an account.
     * @param password The string to be checked if it matches the password of the
     * account of the email.
     * @return If the specified password (after hashing with a salt) is equal to the
     * hashed password saved on the network account database under the account of the given email,
     * returns true; else false.
     * @throws ServerConnectionFailedException If no connection to the network database can be
     * established.
     * @throws AccountNotFoundException If there is no account on the network account database with
     * the specified email.
     */
    @Throws(ServerConnectionFailedException::class, AccountNotFoundException::class)
    suspend fun validateLogin(email: String, password: String): Boolean

    /**
     *
     * @return
     */
    @Throws(ServerConnectionFailedException::class)
    suspend fun authenticateAccount(): Boolean
}