pluginManagement {
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

rootProject.name = "ShobdoDaily"

include(":app")

// Core modules
include(":core:core-model")
include(":core:core-database")
include(":core:core-network")
include(":core:core-datastore")
include(":core:core-ui")
include(":core:core-billing")

// Feature modules (never import each other)
include(":feature:feature-auth")
include(":feature:feature-home")
include(":feature:feature-card")
include(":feature:feature-history")
include(":feature:feature-quiz")
include(":feature:feature-paywall")
