plugins {
    alias(libs.plugins.kite.android.library)
    alias(libs.plugins.kite.android.compose)
    alias(libs.plugins.kite.android.hilt)
}

android {
    namespace = "com.potatosheep.kite.core.common"
}

dependencies {
    implementation(libs.coil)
}