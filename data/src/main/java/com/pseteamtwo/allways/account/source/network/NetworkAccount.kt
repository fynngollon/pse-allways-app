package com.pseteamtwo.allways.account.source.network

/**
 * Representation of a account for saving it in the MySql database.
 * It gets saved with the primary-key email.
 * A pseudonym for the name of the account.
 * And the password with a passwordHash and passwordSalt.
 *
 * @property email Is the primary-key for identifying the account in the database.
 * @property pseudonym Is the pseudonym of the account.
 * @property passwordHash Is the passwordHas.
 * @property passwordSalt Is the passwordSalt.
 * @constructor Creates an instance of the account.
 */
data class NetworkAccount (
    val email: String,
    val pseudonym: String,
    val passwordHash: String,
    val passwordSalt: String
)