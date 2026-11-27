// YfeeCode — developer: @yfeeeee
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
    id("com.chaquo.python")
}

android {
    namespace = "dev.yfeeeee.yfeecode"
    compileSdk = 34

    defaultConfig {
        applicationId = "dev.yfeeeee.yfeecode"
        minSdk = 26
        targetSdk = 34
        // CI sets these so every build is newer than the previous one (needed for in-app updates)
        versionCode = (System.getenv("GITHUB_RUN_NUMBER") ?: "1").toInt()
        versionName = "1.0." + (System.getenv("GITHUB_RUN_NUMBER") ?: "0")
        buildConfigField("String", "GIT_SHA", "\"" + (System.getenv("GITHUB_SHA") ?: "dev") + "\"")
        ndk {
            abiFilters += listOf("arm64-v8a")
        }
    }

    // One fixed key for every build, so a new APK can be installed over the old one.
    signingConfigs {
        getByName("debug") {
            storeFile = file("yfeecode.jks")
            storePassword = "yfeecode"
            keyAlias = "yfeecode"
            keyPassword = "yfeecode"
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
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
        jniLibs.useLegacyPackaging = true // extract native libs so proot can be executed
        resources.excludes += "/META-INF/{AL2.0,LGPL2.1}"
    }
}

chaquopy {
    defaultConfig {
        version = "3.13"
        pip {
            // aiogram 2 pins aiohttp<3.9, which has no wheel for Python 3.13 in the Chaquopy repository,
            // so dependencies are listed by hand (--no-deps). Developer: @yfeeeee
            options("--no-deps")
            install("requests")
            install("urllib3")
            install("charset-normalizer")
            install("idna")
            install("certifi")
            install("pyTelegramBotAPI==4.21.0")
            install("aiogram==2.25.2")
            install("Babel<2.12")
            install("pytz")
            install("magic-filter")
            install("aiohttp==3.10.10")
            install("aiohappyeyeballs")
            install("aiosignal")
            install("attrs")
            install("frozenlist")
            install("multidict")
            install("yarl")
            install("propcache")
            install("pyrogram==2.0.106")
            install("pyaes")
            install("pysocks")
        }
    }
}

dependencies {
    val composeBom = platform("androidx.compose:compose-bom:2024.09.00")
    implementation(composeBom)
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.activity:activity-compose:1.9.2")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.6")
    implementation("com.github.termux.termux-app:terminal-view:v0.118.1")
}
