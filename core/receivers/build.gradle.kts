plugins {
    alias(libs.plugins.kite.android.library)
    alias(libs.plugins.kite.android.hilt)
}

android {
    namespace = "com.potatosheep.kite.core.receivers"
}

dependencies {
    implementation(projects.core.common)
    implementation(projects.core.media)

    implementation(libs.androidx.lifecycle)
}

