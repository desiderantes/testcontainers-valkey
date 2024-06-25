object BuildInfo {
    const val GROUP = "com.valkey"
    const val VERSION = "1.0-SNAPSHOT"
    const val PROJECT_NAME = "testcontainers-valkey"
    const val DESCRIPTION = "Testcontainers Extension for Valkey"
    const val INCEPTION_YEAR = "2024"
    const val VENDOR = "Valkey"
    val TAGS = listOf(
        "valkey",
        "testcontainers",
        "junit",
        "test",
        "docker",
        "container"
    )

    const val PROJECT_OWNER = "desiderantes"

    object Links {
        const val HOST = "github.com"
        const val WEBSITE = "https://$HOST/$PROJECT_OWNER/$PROJECT_NAME"
        const val ISSUE_TRACKER = "https://$HOST/$PROJECT_OWNER/$PROJECT_NAME/issues"
        const val SCM= "https://$HOST/$PROJECT_OWNER/$PROJECT_NAME.git"
        const val PROJECT = "https://$HOST/$PROJECT_OWNER/$PROJECT_NAME"
        const val SCM_CONNECTION = "scm:git:https://$HOST/$PROJECT_OWNER/$PROJECT_NAME.git"
        const val SCM_DEVELOPER_CONNECTION = "scm:git:git@$HOST:$PROJECT_OWNER/$PROJECT_NAME.git"
    }

    object License {
        const val NAME = "Apache License 2.0"
        const val URL = "http://www.apache.org/licenses/LICENSE-2.0"
        const val SPDX = "Apache-2.0"
    }

}