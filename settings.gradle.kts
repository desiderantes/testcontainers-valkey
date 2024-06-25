pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}

plugins {
    //id("org.kordamp.gradle.kordamp-parentbuild") version "3.4.0"
}

rootProject.name = "testcontainers-valkey-root"
include("testcontainers-common")
include("testcontainers-valkey")
include("testcontainers-memcached")