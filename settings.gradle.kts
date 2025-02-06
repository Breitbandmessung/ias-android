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
        maven {
            setUrl("https://jitpack.io")
            content {
                includeGroup("com.github.aasitnikov")
            }
        }
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven {
            setUrl("https://jitpack.io")
            content {
                includeGroup("com.github.aasitnikov")
            }
        }
    }
}

rootProject.name = "IAS Demo"
include(":app")
include(":ias-android-coverage")
include(":ias-android-speed")
include(":ias-android-common")

project(":ias-android-common").projectDir = file("ias-android-speed/ias-android-common/module")
project(":ias-android-speed").projectDir = file("ias-android-speed/module")
