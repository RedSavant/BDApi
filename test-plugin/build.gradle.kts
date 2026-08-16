plugins {
    kotlin("jvm") version "2.3.0-RC"
    id("com.gradleup.shadow") version "9.5.1"
    id("xyz.jpenilla.run-paper") version "2.3.1"
}

group = "fr.redsavantmc"
version = "1.0"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/") {
        name = "papermc-repo"
    }
    maven("https://jitpack.io")
}

dependencies {
    implementation(project(":"))
    compileOnly("io.papermc.paper:paper-api:1.21.11-R0.1-SNAPSHOT")
    compileOnly("org.projectlombok:lombok:1.18.42")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    annotationProcessor("org.projectlombok:lombok:1.18.42")
    implementation("com.github.RedSavant:RSUtils:v1.1.1")

    tasks {
        runServer {
            // Configure the Minecraft version for our task.
            // This is the only required configuration besides applying the plugin.
            // Your plugin's jar (or shadowJar if present) will be used automatically.
            minecraftVersion("1.21.11")
        }
    }

    val targetJavaVersion = 21
    kotlin {
        jvmToolchain(targetJavaVersion)
    }

    tasks.shadowJar {
        configurations = listOf(project.configurations.runtimeClasspath.get())
        archiveClassifier.set("")
    }

    tasks.build {
        dependsOn("shadowJar")
    }

    tasks.processResources {
        val props = mapOf("version" to version)
        inputs.properties(props)
        filteringCharset = "UTF-8"
        filesMatching("plugin.yml") {
            expand(props)
        }
    }
}
