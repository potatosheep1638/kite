plugins {
    alias(libs.plugins.kite.android.library)
    alias(libs.plugins.kite.android.hilt)
}

android {
    namespace = "com.potatosheep.kite.core.datastore"
}

dependencies {
    implementation(projects.core.model)
    implementation(projects.core.datastoreProto)
    implementation(projects.core.model)
    implementation(projects.core.common)

    implementation(libs.androidx.datastore.proto)
}