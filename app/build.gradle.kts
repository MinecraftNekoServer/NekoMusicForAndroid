plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    id("kotlin-kapt")
}

android {
    namespace = "com.neko.music"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.neko.music"
        minSdk = 26
        targetSdk = 36
        versionCode = 35
        versionName = "20260207"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("release") {
            // 配置 v1 v2 v3 v4 签名
            enableV1Signing = true
            enableV2Signing = true
            enableV3Signing = true
            enableV4Signing = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
    ndkVersion = "29.0.14206865"
    buildToolsVersion = "36.1.0 rc1"
}

dependencies {

    implementation(libs.androidx.core.ktx)

    implementation(libs.androidx.lifecycle.runtime.ktx)

    implementation(libs.androidx.activity.compose)

    implementation(platform(libs.androidx.compose.bom))

    implementation(libs.androidx.compose.ui)

    implementation(libs.androidx.compose.ui.graphics)

    implementation(libs.androidx.compose.ui.tooling.preview)

    implementation(libs.androidx.compose.material3)

    implementation(libs.androidx.navigation.compose)

    

    // Ktor

    implementation(libs.ktor.client.core)

    implementation(libs.ktor.client.okhttp)

    implementation(libs.ktor.client.content.negotiation)

    implementation(libs.ktor.client.logging)

    implementation(libs.ktor.serialization.kotlinx.json)

    implementation(libs.kotlinx.serialization.json)

    

    // Coil for image loading

    implementation(libs.coil.compose)

    

    // ExoPlayer for audio playback

    implementation(libs.exoplayer)

    

    // Room database

    implementation(libs.androidx.room.runtime)

    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.compose.foundation.layout)

    kapt(libs.androidx.room.compiler)

    

    // Accompanist Permissions

    implementation(libs.accompanist.permissions)

    

    testImplementation(libs.junit)

    androidTestImplementation(libs.androidx.junit)

    androidTestImplementation(libs.androidx.espresso.core)

    androidTestImplementation(platform(libs.androidx.compose.bom))

    androidTestImplementation(libs.androidx.compose.ui.test.junit4)

    debugImplementation(libs.androidx.compose.ui.tooling)

    debugImplementation(libs.androidx.compose.ui.test.manifest)

}