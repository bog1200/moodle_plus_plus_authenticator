plugins {
    alias(libs.plugins.androidApplication)
}

android {
    namespace = "app.romail.mpp_auth"
    compileSdk = 34

    defaultConfig {
        applicationId = "app.romail.mpp_auth"
        minSdk = 28
        targetSdk = 34
        versionCode = 3
        versionName = "0.0.3"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs.getByName("debug")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        viewBinding = true
    }
    packaging {
        resources {
            excludes.add("META-INF/LICENSE")
            excludes.add("META-INF/NOTICE")
            excludes.add("META-INF/versions/9/OSGI-INF/MANIFEST.MF")
        }
    }

}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.prov)
    implementation(libs.navigation.fragment)
    implementation(libs.activity)
    implementation(libs.jmrtd)
    implementation(libs.jp2.android)
    implementation(libs.jnbis)
    implementation(libs.commons.io)
    implementation(libs.scuba.sc.android)
    implementation(libs.bcpkix.jdk15on)
    implementation(libs.okhttp)
    implementation(libs.auth0.jwtdecode)
    implementation("dev.samstevens.totp:totp:1.7.1")
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}