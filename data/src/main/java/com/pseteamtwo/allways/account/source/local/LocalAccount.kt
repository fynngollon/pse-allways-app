package com.pseteamtwo.allways.account.source.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.pseteamtwo.allways.account.Account

@Entity(
    tableName = "account"
)
internal data class LocalAccount(
    @PrimaryKey val email: String,
    var pseudonym: String,
    var passwordHash: String,
    var passwordSalt: String
)