plugins {
    alias(libs.plugins.kite.android.library)
    alias(libs.plugins.kite.android.compose)
}

android {
    namespace = "com.potatosheep.kite.core.ui"
}

dependencies {
    implementation(projects.core.model)
    implementation(projects.core.designsystem)
    implementation(projects.core.common)
    implementation(projects.core.markdown)

    implementation(libs.coil)
    implementation(libs.coil.compose)
    implementation(libs.coil.gif)

    implementation(libs.kotlinx.datetime)
}