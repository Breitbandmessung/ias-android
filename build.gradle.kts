// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        gradlePluginPortal()
    }
    dependencies {
        classpath(libs.fat.aar.android)
    }
}

plugins {
    alias(libs.plugins.android.application) apply false
}