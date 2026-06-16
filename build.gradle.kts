import groovy.lang.Closure

plugins {
    alias(libs.plugins.gitVersion)
}

group = "com.github.taskeren.ftb-snbt"
version = "1.0-SNAPSHOT"

val gitVersion: Closure<String> by extra

try {
    // don't override the version tag
    if (!hasProperty("version")) {
        version = gitVersion()
    }
} catch (e: Exception) {
    println("Failed to get git version")
    e.printStackTrace()
}

subprojects {
    group = rootProject.group
    version = rootProject.version
}
