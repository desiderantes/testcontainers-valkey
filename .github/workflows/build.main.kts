#!/usr/bin/env kotlin
@file:Repository("https://repo1.maven.org/maven2/")
@file:DependsOn("io.github.typesafegithub:github-workflows-kt:3.0.0")
@file:Repository("https://bindings.krzeminski.it")
@file:Import("../../buildSrc/src/main/kotlin/io/valkey/BuildInfo.kt")
@file:DependsOn("actions:checkout:v4")
@file:DependsOn("actions:setup-java:v4")
@file:DependsOn("gradle:actions__setup-gradle:v3")

import io.github.typesafegithub.workflows.actions.actions.Checkout
import io.github.typesafegithub.workflows.actions.actions.SetupJava
import io.github.typesafegithub.workflows.actions.gradle.ActionsSetupGradle
import io.github.typesafegithub.workflows.domain.RunnerType.UbuntuLatest
import io.github.typesafegithub.workflows.domain.triggers.PullRequest
import io.github.typesafegithub.workflows.dsl.workflow
import io.valkey.BuildInfo

workflow(
	name = "Build",
	on = listOf(PullRequest()),
	sourceFile = __FILE__,
) {
	job(id = "build", runsOn = UbuntuLatest) {
		uses(action = Checkout())
		uses(action = SetupJava(distribution = SetupJava.Distribution.Corretto, javaVersion = BuildInfo.JAVA_VERSION.toString()))
		uses(action = ActionsSetupGradle())
		run(
			name = "Build",
			command = "./gradlew build",
		)
	}
}
