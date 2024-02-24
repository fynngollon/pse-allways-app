package com.pseteamtwo.allways.account.source.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Representation of a account for saving as an element of the [androidx.room.Database]
 * [AccountDatabase]
 * A account is being saved with a email as a primary-key, a pseudonym and the password with a
 * passwordHash and a passwordSalt.
 *
 * @property email Primary key to search for the account
 * @property pseudonym Pseudonym of the account
 * @property passwordHash Password hash
 * @property passwordSalt Password salt
 * @constructor Creates an instance of the class
 */
@Entity(
    tableName = "account"
)
data class LocalAccount(
    @PrimaryKey val email: String,
    var pseudonym: String,
    var passwordHash: String,
    var passwordSalt: String
)