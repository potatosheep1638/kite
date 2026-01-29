plugins {
    alias(libs.plugins.kite.android.application)
    alias(libs.plugins.kite.android.compose)
    alias(libs.plugins.kite.android.hilt)
    alias(libs.plugins.kotlinx.serialization)
}

android {
    namespace = "com.potatosheep.kite.app"

    defaultConfig {
        compileSdk = 36
        applicationId = "com.potatosheep.kite"
        versionCode = 9
        versionName = "0.3.4-beta"

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
    implementation(projects.core.translation)

    implementation(projects.feature.feed.impl)
    implementation(projects.feature.home.impl)
    implementation(projects.feature.post.impl)
    implementation(projects.feature.image.impl)
    implementation(projects.feature.subreddit)
    implementation(projects.feature.user)
    implementation(projects.feature.search.impl)
    implementation(projects.feature.video)
    implementation(projects.feature.settings)
    implementation(projects.feature.bookmark.impl)
    implementation(projects.feature.exception)
    implementation(projects.feature.onboarding.impl)
    implementation(projects.feature.about.impl)

    implementation(libs.androidx.material3.adaptive.navigation.suite.android)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.coil)
    implementation(libs.markwon.core)
    implementation(libs.androidx.datastore.proto)
    implementation(libs.androidx.core.splashscreen)

    testImplementation(libs.junit)

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}