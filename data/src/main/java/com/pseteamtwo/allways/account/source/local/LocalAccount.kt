package com.pseteamtwo.allways.account.source.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "account"
)
data class LocalAccount(
    @PrimaryKey val email: String,
    var pseudonym: String,
    var passwordHash: String,
    var passwordSalt: String
)