plugins {
    kotlin("plugin.serialization")
    id("org.jetbrains.kotlin.android")
    id("com.android.library")
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.pseteamtwo.allways"
    compileSdk = 34

    defaultConfig {
        //applicationId = "com.pseteamtwo.allways"
        minSdk = 23
        targetSdk = 34
        //versionCode = 1
        //versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_19
        targetCompatibility = JavaVersion.VERSION_19
    }
    kotlinOptions {
        jvmTarget = "19"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.9"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    ksp("androidx.room:room-compiler:2.6.1")
    implementation("com.google.dagger:hilt-android:2.50")
    ksp("com.google.dagger:hilt-compiler:2.50")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")
    ksp("androidx.hilt:hilt-compiler:1.2.0")

    // KSP
    implementation("com.google.devtools.ksp:symbol-processing-api:1.9.22-1.0.16")

    // Room - for local databases
    implementation("androidx.room:room-ktx:2.6.1")
    implementation("com.google.android.gms:play-services-location:21.1.0")
    annotationProcessor("androidx.room:room-compiler:2.6.1")
    implementation("androidx.test.ext:junit-ktx:1.1.5")
    ksp("androidx.room:room-compiler:2.6.1")

    //implementation("com.github.hantsy:jsonschema-kotlin:2.4.2")
    implementation ("net.pwall.json:json-kotlin-schema:0.44")

    implementation("org.postgresql:postgresql:42.3.1")
    //implementation("mysql:mysql-connector-java:8.0.23") //Implementierung Treiber für SQL
    //implementation(kotlin("stdlib-jdk8")) //Implementierung Bibliothek

    // Hilt - data injection
    implementation("com.google.dagger:hilt-android:2.50")
    ksp("com.google.dagger:hilt-compiler:2.50")

    // Open-Street-Map
    implementation("org.osmdroid:osmdroid-android:6.1.14")
    implementation("org.osmdroid:osmdroid-wms:6.1.14")
    implementation("org.osmdroid:osmdroid-mapsforge:6.1.14")
    implementation("org.osmdroid:osmdroid-geopackage:6.1.14")

    // TODO either update to android 8 or use this for duration calculation
    implementation("com.jakewharton.threetenabp:threetenabp:1.4.6")

    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0")
    implementation("androidx.activity:activity-ktx:1.8.2")
    implementation("androidx.core:core-ktx:1.12.0")
    // AndroidX Location - for tracking
    //implementation("androidx.location:location-services:2.6.0") TODO not found
    //implementation("androidx.location:location-ktx:2.4.0-beta01") TODO not found

    // Google Play Services - for tracking
    implementation("com.google.android.gms:play-services-location:21.1.0")

    // Json
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")

    // For testing
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.2.1")
    testImplementation("io.mockk:mockk:1.12.0")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")

    // default dependencies
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation(platform("androidx.compose:compose-bom:2023.08.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.08.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}
