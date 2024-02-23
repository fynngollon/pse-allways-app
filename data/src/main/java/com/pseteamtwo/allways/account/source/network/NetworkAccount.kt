package com.pseteamtwo.allways.account.source.network

internal data class NetworkAccount (
    val email: String,
    val pseudonym: String,
    val passwordHash: String,
    val passwordSalt: String
)