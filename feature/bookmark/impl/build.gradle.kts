plugins {
    alias(libs.plugins.kite.android.library)
    alias(libs.plugins.kite.android.compose)
    alias(libs.plugins.kite.android.feature)
    alias(libs.plugins.kotlinx.serialization)
}

android {
    namespace = "com.potatosheep.kite.feature.bookmark.impl"
}

dependencies {
    implementation(projects.core.model)
    implementation(projects.core.data)
    implementation(projects.core.designsystem)
    implementation(projects.core.ui)
    implementation(projects.core.common)
    implementation(projects.core.translation)
    implementation(projects.feature.bookmark.api)
    implementation(projects.feature.post.api)
    implementation(projects.feature.subreddit.api)
    implementation(projects.feature.user.api)
    implementation(projects.feature.image.api)
    implementation(projects.feature.search.api)
    implementation(projects.feature.video.api)

    implementation(libs.kotlinx.datetime)
}