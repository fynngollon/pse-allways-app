plugins {
    id("com.android.application") version "8.2.2" apply false
    //id("com.android.library") version "8.2.2" apply false
    id("org.jetbrains.kotlin.android") version "1.9.22" apply false

    //kotlin("jvm") version "1.9.22" apply false

    //id("com.google.devtools.ksp") version "1.8.10-1.0.9" apply false
    //id("com.google.dagger.hilt.android") version "2.44" apply false
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

