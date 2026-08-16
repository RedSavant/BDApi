plugins {
    id("java-library")
    id("maven-publish")
    id("com.gradleup.shadow") version "9.5.1"
}

group = "fr.redsavant"
version = "1.0.0"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.11-R0.1-SNAPSHOT")
}

tasks {
    compileJava {
        options.encoding = "UTF-8"
        options.release.set(21)
    }
}

tasks.jar {
    archiveBaseName.set("BlockDisplayAPI")
}

tasks.shadowJar {
    archiveBaseName.set("BlockDisplayAPI")
    archiveClassifier.set("shaded")
}

tasks.build {
    dependsOn(tasks.shadowJar)
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
            groupId = "fr.redsavant"
            artifactId = "displayapi"
            version = project.version.toString()
        }
    }
}
