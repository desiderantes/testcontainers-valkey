import com.valkey.configureJReleaser

plugins {
    base
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
