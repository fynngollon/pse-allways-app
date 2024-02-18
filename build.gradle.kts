plugins {
    id("com.android.application") version "8.2.2" apply false
    //id("com.android.library") version "8.2.2" apply false
    id("org.jetbrains.kotlin.android") version "1.9.22" apply false
    id("com.google.devtools.ksp") version "1.9.22-1.0.16" apply false
    id("com.google.dagger.hilt.android") version "2.50" apply false

    //kotlin("jvm") version "1.9.22" apply false
}

buildscript {
    val agp_version by extra("8.2.2")
    repositories {
        google()
        mavenCentral()
    }

    dependencies {
        classpath(kotlin("gradle-plugin", version = "1.9.22"))
    }
}

/*
allprojects {
    repositories {
        google()
        mavenCentral()
    }
}
*/

