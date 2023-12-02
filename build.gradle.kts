plugins {
    id("fabric-loom") version "1.1-SNAPSHOT"
    id("maven-publish")
    kotlin("jvm") version "1.8.10"
    id("com.github.johnrengelman.shadow") version "7.0.0"
    kotlin("plugin.serialization") version "1.9.21"
}

base {
    val archivesBaseName: String by project
    archivesName.set(archivesBaseName)
}

version = project.property("mod_version") as String
group = project.property("maven_group") as String


repositories {
    mavenCentral()
    maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")
    maven("https://maven.terraformersmc.com/")
}

val transitiveInclude: Configuration by configurations.creating {
    exclude(group = "com.mojang")
    exclude(group = "org.jetbrains.kotlin")
    exclude(group = "org.jetbrains.kotlinx")
}


dependencies {
    minecraft("com.mojang:minecraft:${project.property("minecraft_version")}")
    mappings("net.fabricmc:yarn:${project.property("yarn_mappings")}")

    modImplementation("net.fabricmc:fabric-loader:${project.property("loader_version")}")
    modImplementation("net.fabricmc.fabric-api:fabric-api:${project.property("fabric_version")}")
    modImplementation("net.fabricmc:fabric-language-kotlin:${project.property("fabric_kotlin_version")}")

    transitiveInclude(implementation("club.minnced:discord-webhooks:0.8.4")!!)

    transitiveInclude(implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.5.0")!!)
    transitiveInclude(implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")!!)

    transitiveInclude(implementation("com.github.shynixn.mccoroutine:mccoroutine-fabric-api:2.13.0")!!)
    transitiveInclude(implementation("com.github.shynixn.mccoroutine:mccoroutine-fabric-core:2.13.0")!!)

    implementation("net.kyori:adventure-text-minimessage:4.14.0")
    modImplementation(include("net.kyori:adventure-platform-fabric:5.10.1")!!)

    transitiveInclude.resolvedConfiguration.resolvedArtifacts.forEach {
        include(it.moduleVersion.id.toString())
    }
}


tasks {
    val javaVersion = JavaVersion.VERSION_17
    withType<JavaCompile> {
        options.encoding = "UTF-8"
        sourceCompatibility = javaVersion.toString()
        targetCompatibility = javaVersion.toString()
        options.release.set(javaVersion.toString().toInt())
    }
    jar { from("LICENSE") { rename { "${it}_${base.archivesName}" } } }
    processResources {
        inputs.property("version", project.version)
        filesMatching("fabric.mod.json") { expand(mutableMapOf("version" to project.version)) }
    }
    shadowJar {

    }
    build {
    }
    java {
        sourceCompatibility = javaVersion
        targetCompatibility = javaVersion
        withSourcesJar()
    }
}