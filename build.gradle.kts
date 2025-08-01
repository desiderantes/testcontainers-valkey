import io.valkey.configureJReleaser
import io.valkey.BuildInfo

plugins {
    base
    alias(libs.plugins.benmanes.versions)
    id(libs.plugins.jreleaser.get().pluginId)
}



group = BuildInfo.GROUP
version = BuildInfo.VERSION

subprojects {
    group = BuildInfo.GROUP
    version = BuildInfo.VERSION
    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
    }


}

configureJReleaser()
