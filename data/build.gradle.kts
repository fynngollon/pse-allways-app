plugins {
    kotlin("plugin.serialization")
    id("org.jetbrains.kotlin.android")
    id("com.android.application")
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
}

android {
    packagingOptions {
        exclude("META-INF/INDEX.LIST")
    }
    namespace = "com.pseteamtwo.allways"
    compileSdk = 34

    defaultConfig {
        //applicationId = "com.pseteamtwo.allways"
        minSdk = 26
        //targetSdk = 34
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
            jniLibs.pickFirsts.add("lib/armeabi-v7a/libassmidi.so")
            jniLibs.pickFirsts.add("lib/x86/libassmidi.so")
            resources.excludes.apply {
                add("META-INF/AL2.0")
                add("META-INF/LGPL2.1")
                add("META-INF/INDEX.LIST")
                add("META-INF/LICENSE.md")
                add("META-INF/LICENSE-notice.md")
                add("META-INF/DEPENDENCIES")
                add("META-INF/*.properties")
            }
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

}

dependencies {

    // KSP
    testImplementation("com.google.devtools.ksp:symbol-processing-api:1.9.23-1.0.19")

    // Room - for local databases
    implementation("androidx.room:room-ktx:2.6.1")
    implementation("androidx.test.ext:junit-ktx:1.1.5")
    ksp("androidx.room:room-compiler:2.6.1")
    //annotationProcessor("androidx.room:room-compiler:2.6.1")

    // JSON
    implementation ("net.pwall.json:json-kotlin-schema:0.47")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")

    // SQL
    implementation("mysql:mysql-connector-java:8.0.33")
    //implementation(kotlin("stdlib-jdk8"))

    // Hilt - data injection
    implementation("com.google.dagger:hilt-android:2.51")
    //annotationProcessor("com.google.dagger:hilt-compiler:2.51")
    ksp("com.google.dagger:hilt-compiler:2.51")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")
    //ksp("androidx.hilt:hilt-compiler:1.2.0")

    // Open-Street-Map
    implementation("org.osmdroid:osmdroid-android:6.1.18")
    implementation("org.osmdroid:osmdroid-wms:6.1.18")
    implementation("org.osmdroid:osmdroid-mapsforge:6.1.18")
    implementation("org.osmdroid:osmdroid-geopackage:6.1.18")
    implementation("com.github.MKergall:osmbonuspack:6.9.0")

    // Date, Time ...
    implementation("com.jakewharton.threetenabp:threetenabp:1.4.6")

    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0")
    implementation("androidx.activity:activity-ktx:1.8.2")

    // Google Play Services - for tracking
    implementation("com.google.android.gms:play-services-location:21.2.0")

    // For testing
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.2.1")
    testImplementation("io.mockk:mockk:1.13.10")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.8.0")

    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.4")

    // default dependencies
    implementation("androidx.fragment:fragment-ktx:1.6.2")
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation(platform("androidx.compose:compose-bom:2024.02.02"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.recyclerview:recyclerview:1.3.2")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    testImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2024.02.02"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    // From UI
    implementation("androidx.compose.material:material-icons-extended:1.6.3")
    implementation ("androidx.navigation:navigation-compose:2.7.7")
    implementation("androidx.navigation:navigation-runtime-ktx:2.7.7")
    implementation("co.yml:ycharts:2.1.0")

    implementation("com.patrykandpatrick.vico:compose:1.14.0")

    // For `compose`. Creates a `ChartStyle` based on an M2 Material Theme.
    implementation("com.patrykandpatrick.vico:compose-m2:1.14.0")

    // For `compose`. Creates a `ChartStyle` based on an M3 Material Theme.
    implementation("com.patrykandpatrick.vico:compose-m3:1.14.0")

    // Houses the core logic for charts and other elements. Included in all other modules.
    implementation("com.patrykandpatrick.vico:core:1.14.0")
}