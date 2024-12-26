plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.nudha.weatherapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.nudha.weatherapp"
        minSdk = 23
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildToolsVersion = "35.0.0 rc1"
}

dependencies {

    implementation(libs.play.services.location)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.core)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.messaging)
    implementation(libs.google.services)
    implementation(libs.media3.common)
    implementation(libs.espresso.contrib)
    testImplementation(libs.core)
    testImplementation(libs.ext.junit)
    testImplementation(libs.espresso.core)
    androidTestImplementation(libs.rules)

    //Viewmodel
    val lifecycling_version = "2.8.2"
    implementation(libs.lifecycle.livedata.ktx)

    //Google Maps
    implementation (libs.play.services.maps)
    implementation (libs.places)

    implementation(libs.swiperefreshlayout)

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)

    //test implementations
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation("androidx.test.uiautomator:uiautomator:2.3.0")
    testImplementation(libs.mockito.core)
    testImplementation (libs.mockito.inline)
    androidTestImplementation (libs.mockito.android)
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.testng)
    testImplementation("androidx.test:rules:1.6.1")
    testImplementation("com.squareup.okhttp3:mockwebserver:4.12.0")
    testImplementation("org.robolectric:robolectric:4.14.1")

    implementation(libs.glide)
    implementation(libs.retrofit)
    implementation(libs.converter.gson)

    implementation (libs.work.runtime)
    implementation (libs.guava)
}
