plugins {
    alias(libs.plugins.kite.android.feature.api)
}

android {
    namespace = "com.potatosheep.kite.feature.post.api"
}

dependencies {
    api(projects.core.navigation)
}