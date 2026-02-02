plugins {
    alias(libs.plugins.kite.android.library)
    alias(libs.plugins.kite.android.compose)
    alias(libs.plugins.kite.android.feature.impl)
    alias(libs.plugins.kotlinx.serialization)
}

android {
    namespace = "com.potatosheep.kite.feature.searchresult.impl"
}

dependencies {
    implementation(projects.core.data)
    implementation(projects.core.common)
    implementation(projects.core.model)
    implementation(projects.core.designsystem)
    implementation(projects.core.ui)
    implementation(projects.core.domain)
    implementation(projects.core.translation)
    implementation(projects.feature.post.api)
    implementation(projects.feature.subreddit.api)
    implementation(projects.feature.user.api)
    implementation(projects.feature.image.api)
    implementation(projects.feature.searchresult.api)
    implementation(projects.feature.video.api)

    implementation(libs.coil)
    implementation(libs.coil.compose)
    implementation(libs.coil.gif)
}