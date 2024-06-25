import com.valkey.configurePackaging

plugins {
    id("java-library-distribution")
    `maven-publish`

}

java {
    sourceCompatibility = JavaVersion.toVersion(libs.versions.java.get())
    targetCompatibility = JavaVersion.toVersion(libs.versions.java.get())
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(libs.versions.java.get()))
    }
    withSourcesJar()
    withJavadocJar()
}

dependencies {
    api(project(":testcontainers-common"))
    testImplementation(platform(libs.boms.junit))
    testImplementation(libs.junit)
    testImplementation(testFixtures(project(":testcontainers-common")))
}

tasks.test {
    useJUnitPlatform()
}

configurePackaging()