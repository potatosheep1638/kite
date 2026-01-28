pluginManagement {
    includeBuild("build-logic")
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "kite"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
include(":app")
include(":core:network")
include(":core:model")
include(":core:data")
include(":feature")
include(":feature")
include(":feature:feed")
include(":core:ui")
include(":core:designsystem")
include(":feature:subscription")
include(":feature:home")
include(":feature:post")
include(":feature:image")
include(":feature:subreddit")
include(":core:common")
include(":feature:subredditwiki")
include(":feature:user")
include(":feature:search")
include(":core:markdown")
include(":feature:video")
include(":core:domain")
include(":feature:settings")
include(":core:datastore")
include(":core:datastore-proto")
include(":core:database")
include(":feature:bookmark:impl")
include(":feature:bookmark:api")
include(":feature:exception")
include(":feature:onboarding")
include(":feature:about:impl")
include(":feature:about:api")
include(":core:media")
include(":core:notification")
include(":core:translation")
include(":core:navigation")
