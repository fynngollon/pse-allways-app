package com.pseteamtwo.allways.account.source.network

import com.pseteamtwo.allways.account.source.local.LocalAccount

data class NetworkAccount (
    val email: String,
    val pseudonym: String,
    val passwordHash: String,
    val passwordSalt: String
)

fun NetworkAccount.toLocal() = LocalAccount(
    email = email,
    pseudonym = pseudonym,
    passwordHash = passwordHash,
    passwordSalt = passwordSalt
)

fun LocalAccount.toNetwork() = NetworkAccount(
    email = email,
    pseudonym = pseudonym,
    passwordHash = passwordHash,
    passwordSalt = passwordSalt
)