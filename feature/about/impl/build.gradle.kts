plugins {
    alias(libs.plugins.kite.android.library)
    alias(libs.plugins.kite.android.compose)
    alias(libs.plugins.kite.android.feature.impl)
}

android {
    namespace = "com.potatosheep.kite.feature.about.impl"
}

dependencies {
    implementation(projects.core.model)
    implementation(projects.core.data)
    implementation(projects.core.designsystem)
    implementation(projects.core.translation)
    implementation(projects.feature.about.api)
}