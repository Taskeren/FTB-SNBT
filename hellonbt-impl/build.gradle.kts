plugins {
    `java-library`
    `maven-publish`
}

repositories {
    mavenCentral()
}

dependencies {
    api(projects.core)
    api(libs.hellonbt)
    implementation(libs.jspecify)

    testImplementation(platform("org.junit:junit-bom:6.1.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<JavaCompile> {
    options.release.set(17)
    options.javaModuleVersion.set(project.version.toString())
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = "ftb-snbt-hellosnbt"
            version = project.version.toString()
            from(components["java"])
        }
    }
}
