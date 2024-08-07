import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

val uploadKeystoreProperties = Properties()
val uploadKeystorePropertiesFile = rootProject.file("upload-keystore.properties")
if (uploadKeystorePropertiesFile.exists()) {
    uploadKeystoreProperties.load(FileInputStream(uploadKeystorePropertiesFile))
}

android {
    namespace = "com.livefront.processkiller"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.livefront.processkiller"
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("release") {
            keyAlias = uploadKeystoreProperties["keyAlias"] as String?
            keyPassword = uploadKeystoreProperties["keyPassword"] as String?
            storePassword = uploadKeystoreProperties["storePassword"] as String?
            storeFile = rootProject.file("upload.keystore")
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("release")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility(libs.versions.jvmTarget.get())
        targetCompatibility(libs.versions.jvmTarget.get())
    }

    kotlin {
        compilerOptions {
            jvmTarget.set(JvmTarget.fromTarget(libs.versions.jvmTarget.get()))
        }
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(libs.androidx.recyclerview)
    implementation(libs.google.material)
}
