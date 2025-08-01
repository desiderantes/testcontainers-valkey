import io.valkey.configurePackaging

plugins {
    id("java-library-distribution")
    `maven-publish`
}

java {
    sourceCompatibility = JavaVersion.toVersion(libs.versions.java.get())
    targetCompatibility = JavaVersion.toVersion(libs.versions.java.get())
    toolchain {
        languageVersion.set( libs.versions.java.map(JavaLanguageVersion::of))
    }
    withSourcesJar()
    withJavadocJar()
}

dependencies {
    api(project(":testcontainers-common"))
    testImplementation(platform(libs.boms.junit))
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.testcontainers.junit)
    testImplementation(libs.awaitility)
    testImplementation(testFixtures(project(":testcontainers-common")))
    testRuntimeOnly(libs.junit.launcher)
}

tasks.test {
    useJUnitPlatform()
}

configurePackaging()
