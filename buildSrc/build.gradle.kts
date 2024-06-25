plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation(libs.pluginlibs.jreleaser)
    implementation(libs.config4k)
}