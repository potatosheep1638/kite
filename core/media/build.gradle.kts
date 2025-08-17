plugins {
    alias(libs.plugins.kite.android.library)
    alias(libs.plugins.kite.android.hilt)
}

android {
    namespace = "com.potatosheep.kite.core.media"
}

dependencies {
    implementation(projects.core.common)
    implementation(libs.retrofit)
    implementation(libs.androidx.lifecycle)
}
