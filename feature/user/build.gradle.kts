plugins {
    alias(libs.plugins.kite.android.library)
    alias(libs.plugins.kite.android.compose)
    alias(libs.plugins.kite.android.feature)
    alias(libs.plugins.kotlinx.serialization)
}

android {
    namespace = "com.potatosheep.kite.feature.user"
}

dependencies {
    implementation(projects.core.model)
    implementation(projects.core.ui)
    implementation(projects.core.designsystem)
    implementation(projects.core.data)
    implementation(projects.core.common)
    implementation(projects.core.domain)

    implementation(libs.coil)
    implementation(libs.coil.compose)
    implementation(libs.coil.gif)
    implementation(libs.kotlinx.serialization.json)
}