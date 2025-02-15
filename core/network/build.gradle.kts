plugins {
    alias(libs.plugins.kite.android.library)
    alias(libs.plugins.kite.android.hilt)
}

android {
    namespace = "com.potatosheep.kite.core.network"
}

dependencies {
    implementation(projects.core.model)
    implementation(projects.core.common)
    implementation(projects.core.markdown)

    implementation(libs.coil)
    implementation(libs.coil.compose)
    implementation(libs.coil.gif)
    implementation(libs.coil.okhttp)
    implementation(libs.jsoup)
    implementation(libs.retrofit)
    implementation(libs.kotlinx.datetime)
    implementation(libs.moshi)
}