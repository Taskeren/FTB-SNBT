plugins {
    `java-library`
    `maven-publish`
}

repositories {
    mavenCentral()
}

dependencies {
    api(projects.core)
    api(libs.adventure.nbt)
    implementation(libs.jspecify)
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(25))
    }
}

tasks.withType<JavaCompile> {
    options.release.set(21)
    options.javaModuleVersion.set(project.version.toString())
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = "ftb-snbt-kyori"
            version = project.version.toString()
            from(components["java"])
        }
    }
}
