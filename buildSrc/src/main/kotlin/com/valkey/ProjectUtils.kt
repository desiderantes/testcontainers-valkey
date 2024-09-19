package com.valkey

import BuildInfo
import com.typesafe.config.ConfigFactory
import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.get
import org.jreleaser.gradle.plugin.JReleaserExtension
import org.jreleaser.model.Active

fun Project.configurePackaging() {
    this.extensions.configure<PublishingExtension> {
        publications {
            create<MavenPublication>("mavenJava") {
                from(components["java"])
                pom {
                    name.set(project.name)
                    description.set(BuildInfo.DESCRIPTION)
                    url.set(BuildInfo.Links.WEBSITE)
                    inceptionYear.set(BuildInfo.INCEPTION_YEAR)
                    organization {
                        name.set(BuildInfo.VENDOR)
                        url.set(BuildInfo.Links.WEBSITE)
                    }
                    issueManagement {
                        system.set("GitHub")
                        url.set(BuildInfo.Links.ISSUE_TRACKER)
                    }
                    licenses {
                        license {
                            name.set(BuildInfo.License.NAME)
                            url.set(BuildInfo.License.URL)
                            distribution.set("repo")
                        }
                    }
                    scm {
                        connection.set(BuildInfo.Links.SCM_CONNECTION)
                        developerConnection.set(BuildInfo.Links.SCM_DEVELOPER_CONNECTION)
                        url.set(BuildInfo.Links.SCM)
                    }
                    developers {
                        getAuthorsFromFile().map { author ->
                            developer {
                                id.set(author.id)
                                name.set(author.name)
                                email.set(author.email)
                                roles.set(author.roles)
                            }
                        }
                    }
                }
            }
        }
        repositories {
            maven {
                name = "GitHubPackages"
                url = uri("https://maven.pkg.github.com/${BuildInfo.PROJECT_OWNER}/${BuildInfo.PROJECT_NAME}")
                credentials {
                    username = project.findProperty("gpr.user") as String?
                    password = project.findProperty("gpr.key") as String?
                }
            }
        }
    }
}

fun Project.configureJReleaser() {
    this.extensions.configure<JReleaserExtension> {
        project {
            name.set(BuildInfo.PROJECT_NAME)
            description.set(BuildInfo.DESCRIPTION)
            longDescription.set("Testcontainers extension for Valkey")
            links {
                homepage.set(BuildInfo.Links.WEBSITE)
                bugTracker.set(BuildInfo.Links.ISSUE_TRACKER)
                vcsBrowser.set(BuildInfo.Links.SCM)
            }
            authors.set(
                getAuthorsFromFile().map { author ->
                    author.name
                }

            )
            version.set(BuildInfo.VERSION)
            java {
                groupId.set(BuildInfo.GROUP)
                version.set(BuildInfo.JAVA_VERSION.toString())
                multiProject.set(true)
            }
            inceptionYear.set(BuildInfo.INCEPTION_YEAR)
            tags.set(
                BuildInfo.TAGS
            )
            license.set(BuildInfo.License.SPDX)

        }
        release {
            github {
                issueTrackerUrl.set(BuildInfo.Links.ISSUE_TRACKER)
                repoCloneUrl.set(BuildInfo.Links.SCM)
                repoUrl.set(BuildInfo.Links.PROJECT)
                overwrite.set(true)
                repoOwner.set(BuildInfo.PROJECT_OWNER)

                changelog {
                    formatted.set(Active.ALWAYS)
                    preset.set("conventional-commits")
                    contributors {
                        enabled.set(false)
                    }
                    labeler {
                        label.set("dependencies")
                        title.set("regex:^(?:deps(?:\\(.*\\))?!?):\\s.*")
                        order.set(130)
                    }
                    category {
                        title.set("Merge")
                        labels.set(
                            listOf(
                                "merge_pull",
                                "merge_branch"
                            )
                        )
                    }

                    category {
                        title.set("⚙️  Dependencies")
                        key.set("dependencies")
                        order.set(80)
                        labels.set(
                            listOf(
                                "dependencies"
                            )
                        )
                    }
                    hide {
                        categories.set(
                            listOf(
                                "Merge"
                            )
                        )
                    }
                    replacer {
                        search.set("deps: ")
                    }
                }
            }
        }
        signing {
            active.set(Active.ALWAYS)
            armored.set(true)
        }
        deploy {
            maven {
                pomchecker {
                    version.set("1.7.0")
                }
                nexus2 {
                    register("main") {
                        active.set(Active.RELEASE)
                        url.set("https://s01.oss.sonatype.org/service/local")
                        closeRepository.set(true)
                        releaseRepository.set(true)
                        stagingRepositories.set(
                            listOf(
                                "build/repos/local/release"
                            )
                        )
                    }
                }

                github {
                    register("main") {

                        active.set(Active.RELEASE)
                        url.set("https://maven.pkg.github.com/${BuildInfo.PROJECT_OWNER}/${BuildInfo.PROJECT_NAME}")
                        stagingRepositories.set(
                            listOf(
                                "build/repos/local/release"
                            )
                        )
                    }
                }
            }
        }
    }
}


fun Project.getAuthorsFromFile(): List<Author> {
    ConfigFactory.parseFile(rootProject.layout.projectDirectory.file("AUTHORS.hocon").asFile).let {
        val authors = it.getConfig("authors")
        return it.getObject("authors").keys.map { key ->
            Author(
                key,
                authors.getConfig(key).getString("name"),
                authors.getConfig(key).getString("email"),
                authors.getConfig(key).getStringList("roles"),
                authors.getConfig(key).getString("notes")
            )
        }
    }
}

data class Author(
    val id: String,
    val name: String,
    val email: String,
    val roles: List<String> = listOf(),
    val notes: String
)