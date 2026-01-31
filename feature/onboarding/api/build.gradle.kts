plugins {
    alias(libs.plugins.kite.android.feature.api)
}

android {
    namespace = "com.potatosheep.kite.feature.onboarding.api"
}

dependencies {
    api(projects.core.navigation)
}