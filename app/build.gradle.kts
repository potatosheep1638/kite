plugins {
    alias(libs.plugins.kite.android.application)
    alias(libs.plugins.kite.android.compose)
    alias(libs.plugins.kite.android.hilt)
    alias(libs.plugins.kotlinx.serialization)
}

android {
    namespace = "com.potatosheep.kite.app"

    defaultConfig {
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
    implementation(projects.core.navigation)

    implementation(projects.feature.feed.impl)
    implementation(projects.feature.feed.api)
    implementation(projects.feature.home.impl)
    implementation(projects.feature.home.api)
    implementation(projects.feature.post.impl)
    implementation(projects.feature.post.api)
    implementation(projects.feature.image.impl)
    implementation(projects.feature.image.api)
    implementation(projects.feature.subreddit.impl)
    implementation(projects.feature.subreddit.api)
    implementation(projects.feature.user.impl)
    implementation(projects.feature.user.api)
    implementation(projects.feature.search.impl)
    implementation(projects.feature.search.api)
    implementation(projects.feature.video.impl)
    implementation(projects.feature.video.api)
    implementation(projects.feature.settings.impl)
    implementation(projects.feature.settings.api)
    implementation(projects.feature.bookmark.impl)
    implementation(projects.feature.bookmark.api)
    implementation(projects.feature.exception)
    implementation(projects.feature.onboarding.impl)
    implementation(projects.feature.onboarding.api)
    implementation(projects.feature.about.impl)
    implementation(projects.feature.about.api)

    implementation(libs.androidx.material3.adaptive.navigation.suite.android)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.coil)
    implementation(libs.markwon.core)
    implementation(libs.androidx.datastore.proto)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.navigation3.ui)
    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.lifecycle.viewmodel.navigation3)

    testImplementation(libs.junit)

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}