plugins {
    `java-library`
    `maven-publish`
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.guava)
    implementation(libs.jspecify)
}

tasks.withType<JavaCompile> {
    options.release.set(17)
    options.javaModuleVersion.set(project.version.toString())
}

tasks.withType<Jar> {
    from(project.file("LICENSE.md")) {
        into("META-INF")
    }
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
