plugins {
    alias(libs.plugins.kite.android.library)
    alias(libs.plugins.kite.android.compose)
}

android {
    namespace = "com.potatosheep.kite.core.designsystem"
}

dependencies {
    implementation(projects.core.common)

    implementation(libs.coil)
    implementation(libs.coil.compose)
    implementation(libs.coil.gif)
    implementation(libs.androidx.material3.adaptive.navigation.suite.android)
}
