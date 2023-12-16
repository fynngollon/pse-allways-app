package com.pseteamtwo.allways.user.dto

/**
 * TODO
 * A special kind of class used to transport user registration data between different parts of the app.
 * Holds information like email, password, and access code during the registration process.
 *
 * What is DTO?
 * DTO stands for "Data Transfer Object."
 * It is a design pattern used in software development to transfer data between software application
 * subsystems or layers that may have different data structures. The main purpose of a DTO is to
 * encapsulate data and send it from one part of the program to another, often across a network
 * or between different components.
 * In the context of the structure provided here, the RegistrationDto.kt class is a
 * Data Transfer Object. It is specifically designed to carry data related to user registration
 * (such as email, password, and access code) from the user interface (or controller) to the service
 * layer. The DTO helps in keeping the communication between different parts of the application
 * organized and ensures that the necessary information is passed accurately.
 */
data class RegistrationDto (
    val email: String,
    val password: String,
    val accessCode: String
    // Other registration fields
)