plugins {
    alias(libs.plugins.kite.android.library)
    alias(libs.plugins.kite.android.hilt)
}

android {
    namespace = "com.potatosheep.kite.core.notification"
}

dependencies {
    implementation(projects.core.common)
    implementation(projects.core.designsystem)
    implementation(projects.core.translation)

    implementation(libs.androidx.lifecycle)
}