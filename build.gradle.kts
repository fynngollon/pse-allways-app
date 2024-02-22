plugins {
    id("com.android.application") version "8.2.2" apply false
    id("org.jetbrains.kotlin.android") version "1.9.22" apply false
    id("org.jetbrains.kotlin.multiplatform") version "1.9.22" apply false
    id("com.google.devtools.ksp") version "1.9.22-1.0.16" apply false
    id("com.google.dagger.hilt.android") version "2.50" apply false
    kotlin("plugin.serialization") version "1.9.22" apply false
    kotlin("jvm") version "1.9.22" apply false
}

buildscript {
    val agp_version by extra("8.2.2")
    repositories {
        google() // Add this line to include the Google Maven repository

        // other repositories if any
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
    // other configurations for the build script
    dependencies {
        classpath(kotlin("gradle-plugin", version = "1.9.22"))
        classpath("org.jetbrains.kotlin:kotlin-serialization:1.9.22")
    }
}


apply(plugin = "org.jetbrains.kotlin.plugin.serialization")

