plugins {
    alias(libs.plugins.kite.android.application)
    alias(libs.plugins.kite.android.compose)
    alias(libs.plugins.kite.android.hilt)
    alias(libs.plugins.kotlinx.serialization)
}

android {
    namespace = "com.potatosheep.kite.app"

    defaultConfig {
        compileSdk = 35
        applicationId = "com.potatosheep.kite"
        versionCode = 4
        versionName = "0.1.0-beta"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        debug {
            isDebuggable = true
            // isMinifyEnabled = true
            // isShrinkResources = true

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
}

dependencies {
    implementation(libs.material)

    implementation(projects.core.model)
    implementation(projects.core.data)
    implementation(projects.core.designsystem)
    implementation(projects.core.common)
    implementation(projects.core.markdown)

    implementation(projects.feature.feed)
    implementation(projects.feature.library)
    implementation(projects.feature.post)
    implementation(projects.feature.image)
    implementation(projects.feature.subreddit)
    implementation(projects.feature.user)
    implementation(projects.feature.search)
    implementation(projects.feature.video)
    implementation(projects.feature.settings)
    implementation(projects.feature.bookmark)
    implementation(projects.feature.exception)
    implementation(projects.feature.onboarding)
    implementation(projects.feature.about)

    implementation(libs.androidx.material3.adaptive.navigation.suite.android)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.coil)
    implementation(libs.markwon.core)
    implementation(libs.androidx.datastore.proto)

    testImplementation(libs.junit)

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}