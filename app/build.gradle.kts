// =====================================================================
// File: build.gradle.kts (App Level)
// Tujuan: Mengonfigurasi build, dependensi, setelan compile, dan tanda tangan digital otomatis (signing release) aplikasi WhatsappIn
// Dipakai oleh: Gradle Build System
// Dependensi Utama: Plugins (com.android.application, org.jetbrains.kotlin.android, compose)
// Daftar Fungsi: Konfigurasi android { namespace, compileSdk, defaultConfig, signingConfigs, buildTypes }, dependencies
// Side Effect: Menghasilkan paket APK aplikasi bertanda tangan digital resmi, menentukan tingkat SDK minimal & target compile
// =====================================================================

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    namespace = "com.heytayo.whatsappin"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.heytayo.whatsappin"
        minSdk = 26
        targetSdk = 35
        versionCode = 2
        versionName = "1.0.1"
    }

    signingConfigs {
        create("release") {
            storeFile = file("whatsappin.jks")
            storePassword = "whatsappin123"
            keyAlias = "whatsappin_key"
            keyPassword = "whatsappin123"
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            signingConfig = signingConfigs.getByName("release")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
    }
}

dependencies {
    // Compose BOM
    val composeBom = platform("androidx.compose:compose-bom:2024.12.01")
    implementation(composeBom)

    // Compose UI
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")

    // Activity Compose
    implementation("androidx.activity:activity-compose:1.9.3")

    // Lifecycle & ViewModel
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.7")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")

    // Google ML Kit - Text Recognition (offline)
    implementation("com.google.mlkit:text-recognition:16.0.1")

    // Jetpack DataStore Preferences
    implementation("androidx.datastore:datastore-preferences:1.1.1")

    // Core KTX
    implementation("androidx.core:core-ktx:1.15.0")

    // Debug
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}
