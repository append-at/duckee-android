@file:Suppress("UnstableApiUsage")

pluginManagement {
    includeBuild("build-logic")
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven { url = uri("https://sdk-download.airbridge.io/maven") }
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)

    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
        maven { url = uri("https://sdk-download.airbridge.io/maven") }
    }
}

rootProject.name = "duckee-android"
include(":app")
include(":core:designsystem")
include(":core:navigation")
include(":core:ui")
include(":core:model")
include(":core:network:api")
include(":core:network:impl")
include(":core:data:api")
include(":core:data:impl")
include(":core:domain")
include(":core:datastore:api")
include(":core:datastore:impl")
include(":feature:explore")
include(":feature:signin")
include(":feature:detail")
include(":feature:recipe")
include(":feature:collection")