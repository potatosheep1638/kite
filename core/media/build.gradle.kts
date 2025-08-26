plugins {
    alias(libs.plugins.kite.android.library)
    alias(libs.plugins.kite.android.hilt)
    alias(libs.plugins.jetbrains.kotlin.parcelize)
}

android {
    namespace = "com.potatosheep.kite.core.media"
}

dependencies {
    implementation(projects.core.common)
    implementation(projects.core.designsystem)
    implementation(projects.core.notification)

    implementation(libs.retrofit)
    implementation(libs.androidx.lifecycle)
}
