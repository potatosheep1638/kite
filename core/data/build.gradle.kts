plugins {
    alias(libs.plugins.kite.android.library)
    alias(libs.plugins.kite.android.hilt)
}

android {
    namespace = "com.potatosheep.kite.core.data"
}

dependencies {
    implementation(projects.core.model)
    implementation(projects.core.network)
    implementation(projects.core.common)
    implementation(projects.core.datastore)
    implementation(projects.core.database)
    implementation(projects.core.media)

    implementation(libs.kotlinx.datetime)
    implementation(libs.moshi)
    implementation(libs.moshi.adapters)
    implementation(libs.moshi.kotlin.codegen)
    implementation(libs.androidx.lifecycle)
}