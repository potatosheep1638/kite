plugins {
    alias(libs.plugins.kite.android.library)
    alias(libs.plugins.kite.android.compose)
    alias(libs.plugins.kite.android.feature.impl)
}

android {
    namespace = "com.potatosheep.kite.feature.exception"
}

dependencies {
    implementation(projects.core.designsystem)
    implementation(projects.core.common)
    implementation(projects.core.translation)

    implementation(libs.coil)
    implementation(libs.coil.compose)
    implementation(libs.coil.gif)
}