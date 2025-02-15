plugins {
    alias(libs.plugins.kite.android.library)
    alias(libs.plugins.kite.android.compose)
    alias(libs.plugins.kite.android.feature)
    alias(libs.plugins.kotlinx.serialization)
}

android {
    namespace = "com.potatosheep.kite.feature.library"
}

dependencies {
    implementation(projects.core.model)
    implementation(projects.core.data)
    implementation(projects.core.designsystem)
    implementation(projects.core.ui)
    implementation(projects.core.common)

    implementation(libs.kotlinx.serialization.json)
}