import groovy.lang.Closure

plugins {
    java
    `maven-publish`
    id("com.palantir.git-version") version "5.0.0"
}

group = "cn.elytra"
version = "1.0-SNAPSHOT"

val gitVersion: Closure<String> by extra

try {
    version = gitVersion()
} catch (e: Exception) {
    println("Failed to get git version")
    e.printStackTrace()
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.glavo:HelloNBT:0.3.0")
    implementation("com.google.guava:guava:33.6.0-jre")
    implementation("org.jspecify:jspecify:1.0.0")

    testImplementation(platform("org.junit:junit-bom:6.0.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<JavaCompile> {
    options.release.set(17)
    options.javaModuleVersion.set(project.version.toString())
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "cn.elytra"
            artifactId = "ftb-snbt"
            version = project.version.toString()

            from(components["java"])
        }
    }
}
