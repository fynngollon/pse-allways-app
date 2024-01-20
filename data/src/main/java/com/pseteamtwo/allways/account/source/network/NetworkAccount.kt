package com.pseteamtwo.allways.account.source.network

import com.pseteamtwo.allways.account.source.local.LocalAccount

data class NetworkAccount (
    val email: String,
    val pseudonym: String,
    val passwordHash: String,
    val passwordSalt: String
)