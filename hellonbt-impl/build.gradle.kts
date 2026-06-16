plugins {
    `java-library`
    `maven-publish`
}

repositories {
    mavenCentral()
}

dependencies {
    api(project(":core"))
    api("org.glavo:HelloNBT:0.3.0")
    implementation("org.jspecify:jspecify:1.0.0")

    testImplementation(platform("org.junit:junit-bom:6.1.0"))
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
