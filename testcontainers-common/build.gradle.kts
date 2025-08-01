plugins {
    id("java-library")
    `java-test-fixtures`
}

java {
    sourceCompatibility = JavaVersion.toVersion(libs.versions.java.get())
    targetCompatibility = JavaVersion.toVersion(libs.versions.java.get())
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(libs.versions.java.get()))
    }
}

dependencies {
    api(libs.memcached)
    api(libs.valkey.java)
    api(platform(libs.boms.testcontainers))
    api(libs.testcontainers.all)
    api(libs.slf4j.api)
    testFixturesImplementation(platform(libs.boms.junit))
    testFixturesImplementation(libs.testcontainers.junit)
    testFixturesImplementation(libs.junit.jupiter)
    testFixturesApi(libs.slf4j.simple)

    //testFixturesImplementation(libs.memcached)
}

tasks.test {
    useJUnitPlatform()
}