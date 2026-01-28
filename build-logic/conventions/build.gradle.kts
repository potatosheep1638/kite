import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    `kotlin-dsl`
}

group = "com.potatosheep.kite.buildlogic"

// Configure the build-logic plugins to target JDK 17
// This matches the JDK used to build the project, and is not related to what is running on device.
java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_17
    }
}

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.ksp.gradlePlugin)
    compileOnly(libs.compose.gradlePlugin)
}

tasks {
    validatePlugins {
        enableStricterValidation = true
        failOnWarning = true
    }
}

gradlePlugin {
    plugins {
        register("androidHilt") {
            id = "kite.android.hilt"
            implementationClass = "AndroidHiltConventionPlugin"
        }
        register("androidCompose") {
            id = "kite.android.compose"
            implementationClass = "AndroidComposeConventionPlugin"
        }
        register("androidApplication") {
            id = "kite.android.application"
            implementationClass = "AndroidApplicationConventionPlugin"
        }

        // TODO: Remove this
        register("androidFeature") {
            id = "kite.android.feature"
            implementationClass = "AndroidFeatureConventionPlugin"
        }

        register("androidFeatureApi") {
            id = libs.plugins.kite.android.feature.api.get().pluginId
            implementationClass = "AndroidFeatureApiConventionPlugin"
        }

        register("androidFeatureImpl") {
            id = libs.plugins.kite.android.feature.impl.get().pluginId
            implementationClass = "AndroidFeatureImplConventionPlugin"
        }

        register("androidLibrary") {
            id = "kite.android.library"
            implementationClass = "AndroidLibraryConventionPlugin"
        }

        register("jvmLibrary") {
            id = "kite.jvm.library"
            implementationClass = "JvmLibraryConventionPlugin"
        }
    }
}