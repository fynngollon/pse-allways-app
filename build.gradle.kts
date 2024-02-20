// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.2.0" apply false
    id("org.jetbrains.kotlin.android") version "1.9.0" apply false
    id("com.google.devtools.ksp") version "1.8.10-1.0.9" apply false
    id("com.google.dagger.hilt.android") version "2.44" apply false

}
buildscript {
    repositories {
        google() // Add this line to include the Google Maven repository

        // other repositories if any
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
    // other configurations for the build script
}

allprojects {
    repositories {
        google() // Add this line to include the Google Maven repository

        // other repositories if any
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}
