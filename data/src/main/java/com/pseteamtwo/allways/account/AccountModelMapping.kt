package com.pseteamtwo.allways.account

import com.pseteamtwo.allways.account.source.local.LocalAccount
import com.pseteamtwo.allways.account.source.network.NetworkAccount

/**
 * Data model mapping extension functions. There are three model types:
 *
 * - Account: External model exposed to other layers in the architecture.
 * Obtained using `toExternal`.
 *
 * - NetworkAccount: Internal model used to represent an account from the network.
 * Obtained using `toNetwork`.
 *
 * - LocalAccount: Internal model used to represent an account stored locally in a database.
 * Obtained using `toLocal`.
 */


/**
 * Account: local to external
 *
 * Converts a [LocalAccount] into an external [Account] to expose it
 * to other layers in the architecture.
 *
 * @receiver [LocalAccount]
 */
internal fun LocalAccount.toExternal() = Account(
    email = email,
    pseudonym = pseudonym
)


/**
 * Account: network to local
 *
 * Converts a [NetworkAccount] into a [LocalAccount] to store it into
 * the local database afterwards.
 *
 * @receiver [NetworkAccount]
 */
internal fun NetworkAccount.toLocal() = LocalAccount(
    email = email,
    pseudonym = pseudonym,
    passwordHash = passwordHash,
    passwordSalt = passwordSalt
)

/**
 * Account: local to network
 *
 * Converts a [LocalAccount] into a [NetworkAccount] to store it into
 * the network database afterwards.
 *
 * @receiver [LocalAccount]
 */
internal fun LocalAccount.toNetwork() = NetworkAccount(
    email = email,
    pseudonym = pseudonym,
    passwordHash = passwordHash,
    passwordSalt = passwordSalt
)