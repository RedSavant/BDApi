plugins {
    id("java-library")
    id("maven-publish")
    id("com.gradleup.shadow") version "9.5.1"
}

group = "fr.redsavant"
version = "1.1.0-beta.1"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.codemc.io/repository/maven-releases/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.11-R0.1-SNAPSHOT")
    compileOnly("com.github.retrooper:packetevents-spigot:2.9.5")

    testImplementation("org.junit.jupiter:junit-jupiter:5.11.4")
    testImplementation("org.mockito:mockito-core:5.14.2")
    testImplementation("io.papermc.paper:paper-api:1.21.11-R0.1-SNAPSHOT")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks {
    compileJava {
        options.encoding = "UTF-8"
        options.release.set(21)
    }
}

tasks.test {
    useJUnitPlatform()
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
            artifactId = "bdapi"
            version = project.version.toString()
        }
    }
    repositories {
        maven("https://maven.rscomeback.fr/releases") {
            name = "reposilite"
            credentials {
                username = System.getenv("REPOSILITE_TOKEN_NAME")
                password = System.getenv("REPOSILITE_TOKEN_SECRET")
            }
        }
    }
}
