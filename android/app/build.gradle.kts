plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.stalkerone.depthkeyboard"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.stalkerone.depthkeyboard"
        minSdk = 26
        targetSdk = 35
        versionCode = 2
        versionName = "1.1.0"
    }
}

kotlin { jvmToolchain(17) }
