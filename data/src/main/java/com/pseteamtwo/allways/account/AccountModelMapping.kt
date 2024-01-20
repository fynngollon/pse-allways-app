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

//localToExternal
fun LocalAccount.toExternal() = Account(
    email = email,
    pseudonym = pseudonym
)

//networkToLocal
fun NetworkAccount.toLocal() = LocalAccount(
    email = email,
    pseudonym = pseudonym,
    passwordHash = passwordHash,
    passwordSalt = passwordSalt
)

//localToNetwork
fun LocalAccount.toNetwork() = NetworkAccount(
    email = email,
    pseudonym = pseudonym,
    passwordHash = passwordHash,
    passwordSalt = passwordSalt
)