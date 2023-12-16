package com.pseteamtwo.allways.user.model

/**
 * TODO
 * Represents the structure of a user in the app.
 * Includes information like email, password, and any other details associated with a user.
 *
 * Will look different to RegistrationDto.kt when implemented!
 */
data class User(
    val id: Long? = null,
    val email: String,
    val password: String
    // Other user fields
)