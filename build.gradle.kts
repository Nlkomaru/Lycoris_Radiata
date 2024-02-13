plugins {
    id("fabric-loom") version "1.4-SNAPSHOT"
    id("maven-publish")
    kotlin("jvm") version "1.9.10"
    id("com.github.johnrengelman.shadow") version "8.1.1"
    kotlin("plugin.serialization") version "1.9.10"
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
    transitiveInclude(implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")!!)

    transitiveInclude(implementation("com.github.shynixn.mccoroutine:mccoroutine-fabric-api:2.13.0")!!)
    transitiveInclude(implementation("com.github.shynixn.mccoroutine:mccoroutine-fabric-core:2.13.0")!!)

    implementation("net.kyori:adventure-text-minimessage:4.14.0")
    modImplementation(include("net.kyori:adventure-platform-fabric:5.11.0")!!)

    transitiveInclude.resolvedConfiguration.resolvedArtifacts.forEach {
        include(it.moduleVersion.id.toString())
    }
}

tasks {
    val javaVersion = JavaVersion.VERSION_17
    compileKotlin {
        kotlinOptions.jvmTarget = "17"
        kotlinOptions.javaParameters = true
    }
    build {
        dependsOn(shadowJar)
    }
    processResources {
        inputs.property("version", project.version)
        filesMatching("fabric.mod.json") { expand(mutableMapOf("version" to project.version)) }
    }
    jar {
        from("LICENSE") {
            rename {
                "${it}_${base.archivesName}"
            }
        }
    }
    withType<JavaCompile>().configureEach {
        options.encoding = "UTF-8"
        sourceCompatibility = javaVersion.toString()
        targetCompatibility = javaVersion.toString()
        options.release.set(javaVersion.toString().toInt())
    }
    java {
        sourceCompatibility = javaVersion
        targetCompatibility = javaVersion
        withSourcesJar()
    }
}
