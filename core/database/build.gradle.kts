plugins {
    alias(libs.plugins.kite.android.library)
    alias(libs.plugins.kite.android.hilt)
}

android {
    namespace = "com.potatosheep.kite.core.database"
}

dependencies {
    implementation(projects.core.model)
    implementation(projects.core.common)

    implementation(libs.moshi)
    implementation(libs.moshi.adapters)
    implementation(libs.moshi.kotlin)

    implementation(libs.kotlinx.datetime)

    implementation(libs.androidx.room)
    implementation(libs.androidx.room.ktx)
    ksp(libs.ksp.androidx.room)
}