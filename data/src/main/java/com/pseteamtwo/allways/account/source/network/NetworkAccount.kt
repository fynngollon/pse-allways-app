package com.pseteamtwo.allways.account.source.network

data class NetworkAccount (
    val email: String,
    val pseudonym: String,
    val passwordHash: String,
    val passwordSalt: String
)