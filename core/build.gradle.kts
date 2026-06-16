plugins {
    `java-library`
    `maven-publish`
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.google.guava:guava:33.6.0-jre")
    implementation("org.jspecify:jspecify:1.0.0")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = "ftb-snbt-core"
            version = project.version.toString()
            from(components["java"])
        }
    }
}
