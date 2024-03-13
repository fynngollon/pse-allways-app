package com.pseteamtwo.allways.data.account

/**
 * Representation of a user account (to be also used outside this data module)
 *
 * A user account can be created by the user when registering while not already logged into
 * an account.
 * There may only be a maximum of one account logged in in this application at the same time.
 *
 * @property pseudonym The pseudonymized [email]. This unique pseudonym is used to access the
 * data of this user account on the network database.
 * @property email The unique email entered by the user to identify the account on the network
 * account database and used to generate the [pseudonym].
 * @constructor Creates an account with the specified properties.
 */
data class Account(
    val pseudonym: String,
    val email: String
)
