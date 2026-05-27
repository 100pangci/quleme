plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.jetbrains.kotlin.compose)
    alias(libs.plugins.google.dagger.hilt.android)
    alias(libs.plugins.google.ksp)
}

fun computeVersionCode(versionName: String): Int {
    val parts = versionName.split(".", "-", "_")
    val numbers = parts.mapNotNull { it.toIntOrNull() }
    val major = numbers.getOrElse(0) { 0 }
    val minor = numbers.getOrElse(1) { 0 }
    val patch = numbers.getOrElse(2) { 0 }
    return major * 10000 + minor * 100 + patch
}

val versionNameFromFile = rootProject.file("VERSION")
    .takeIf { it.exists() }
    ?.readText()
    ?.trim()
    ?.takeIf { it.isNotEmpty() }
    ?: "1.0.0"

val versionCodeFromFile = computeVersionCode(versionNameFromFile)

ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
    arg("room.incremental", "true")
}

android {
    namespace = "com.luleme"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "com.luleme"
        minSdk = 26
        targetSdk = 36
        versionCode = versionCodeFromFile
        versionName = versionNameFromFile

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    signingConfigs {
        create("release") {
            val keyStorePath = System.getenv("SIGNING_KEY_STORE_PATH")
            val keyStorePassword = System.getenv("SIGNING_STORE_PASSWORD")
            val keyAlias = System.getenv("SIGNING_KEY_ALIAS")
            val keyPassword = System.getenv("SIGNING_KEY_PASSWORD")
            
            if (!keyStorePath.isNullOrEmpty() && !keyStorePassword.isNullOrEmpty()) {
                storeFile = file(keyStorePath)
                storePassword = keyStorePassword
                this.keyAlias = keyAlias
                this.keyPassword = keyPassword
            }
        }
    }

    buildTypes {
        release {
            val releaseSigning = signingConfigs.getByName("release")
            if (releaseSigning.storeFile != null) {
                signingConfig = releaseSigning
            }
            isMinifyEnabled = true
            isShrinkResources = true
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
        buildConfig = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.fragment)
    implementation(libs.androidx.navigation.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.androidx.biometric)
    
    // Hilt
    implementation(libs.hilt.android)
    implementation(libs.hilt.lifecycle.viewmodel.compose)
    ksp(libs.hilt.compiler)

    // Room
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)

    // Gson
    implementation(libs.gson)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}
