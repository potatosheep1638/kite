plugins {
    alias(libs.plugins.kite.android.feature.api)
}

android {
    namespace = "com.potatosheep.kite.feature.searchresult.api"
}

dependencies {
    implementation(projects.core.common)
    api(projects.core.navigation)
}