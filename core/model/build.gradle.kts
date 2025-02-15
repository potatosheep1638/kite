plugins {
    alias(libs.plugins.kite.android.library)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.potatosheep.kite.core.model"
}

dependencies {
    implementation(projects.core.common)

    implementation(libs.kotlinx.datetime)

    implementation(libs.moshi.kotlin)
    ksp(libs.moshi.kotlin.codegen)
}
