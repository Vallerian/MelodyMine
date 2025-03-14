import org.sayandev.plugin.StickyNoteModules
import xyz.jpenilla.runpaper.task.RunServer
import java.net.URL
import java.util.concurrent.Executors

plugins {
    kotlin("jvm") version "2.1.10"
    id("maven-publish")
    id("org.jetbrains.gradle.plugin.idea-ext") version "1.1.9"
    id("xyz.jpenilla.run-paper") version "2.3.1"
    id("org.sayandev.stickynote.project")
}

stickynote {
    modules(StickyNoteModules.BUKKIT, StickyNoteModules.BUKKIT_NMS)
    relocate(!gradle.startParameter.taskNames.any { it.startsWith("runServer") })
}

group = "ir.taher7"
version = "${project.version}"
description = "Minecraft voice chat plugin"

repositories {
    mavenLocal()
    mavenCentral()

    maven("https://repo.sayandev.org/snapshots")
    maven("https://repo.sayandev.org/releases")
    maven("https://repo.sayandev.org/private")

    maven("https://jitpack.io")

    maven("https://repo.maven.apache.org/maven2/")

    // Velocity-API / PaperLib / Folia
    maven("https://repo.papermc.io/repository/maven-public/")

    // AdventureAPI/MiniMessage
    maven("https://oss.sonatype.org/content/repositories/snapshots/")

    // Spigot
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://oss.sonatype.org/content/repositories/snapshots")
    maven("https://oss.sonatype.org/content/repositories/central")

    // PlaceholderAPI
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")

    // Update checker
    maven("https://repo.jeff-media.com/public/")

    // ProtocolLib
    maven("https://repo.dmulloy2.net/repository/public/")
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.19.4-R0.1-SNAPSHOT")
    compileOnly("me.clip:placeholderapi:2.11.6")

    stickynote.implementation("org.bstats:bstats-bukkit:3.0.2")

    stickynote.implementation("com.jeff_media:SpigotUpdateChecker:3.0.3")

    // Socket Io
    stickynote.implementation("io.socket:socket.io-client:2.1.1")
    stickynote.implementation("com.github.kenglxn.QRGen:javase:3.0.1")

    compileOnly("com.comphenix.protocol:ProtocolLib:5.1.0")
    stickynote.implementation("org.slf4j:slf4j-api:1.7.25")
    stickynote.implementation("ch.qos.logback:logback-core:1.5.12")
    stickynote.implementation("ch.qos.logback:logback-classic:1.5.12")
    stickynote.implementation("xerces:xercesImpl:2.12.2")
}

java {
    withSourcesJar()
}

tasks {
    withType(RunServer::class.java) {
        downloadPlugins {
            modrinth("viaversion", "5.0.3")
            modrinth("essentialsx", "2.20.1")
            hangar("placeholderapi", "2.11.6")
            url("https://download.luckperms.net/1567/bukkit/loader/LuckPerms-Bukkit-5.4.150.jar")
            url("https://github.com/MilkBowl/Vault/releases/download/1.7.3/Vault.jar")
            url("https://ci.lucko.me/job/spark/471/artifact/spark-bukkit/build/libs/spark-1.10.123-bukkit.jar")
        }
    }

    runServer {
        minecraftVersion("1.21.1")

        javaLauncher = project.javaToolchains.launcherFor {
            vendor = JvmVendorSpec.JETBRAINS
            languageVersion = JavaLanguageVersion.of("21")
        }

        jvmArgs("-XX:+AllowEnhancedClassRedefinition")
    }

    shadowJar {
        exclude("META-INF/**")
        archiveFileName.set("${project.name}-${version}.jar")
        from("LICENSE")
    }


    compileJava {
        options.encoding = Charsets.UTF_8.name()
    }

    processResources {
        filteringCharset = Charsets.UTF_8.name()
        filesMatching("plugin.yml") {
            expand("version" to version)
        }
    }

    java {
        if (gradle.startParameter.getTaskNames().isNotEmpty() && (gradle.startParameter.getTaskNames()
                .contains("runServer") || gradle.startParameter.getTaskNames().contains("runFolia"))
        ) {
            toolchain.languageVersion = JavaLanguageVersion.of(21)
        } else {
            toolchain.languageVersion = JavaLanguageVersion.of(17)
        }
    }

    build {
        //dependsOn(shadowJar)
    }
}

configurations {
    "apiElements" {
        attributes {
            attribute(Usage.USAGE_ATTRIBUTE, project.objects.named(Usage.JAVA_API))
            attribute(Category.CATEGORY_ATTRIBUTE, project.objects.named(Category.LIBRARY))
            attribute(Bundling.BUNDLING_ATTRIBUTE, project.objects.named(Bundling.SHADOWED))
            attribute(LibraryElements.LIBRARY_ELEMENTS_ATTRIBUTE, project.objects.named(LibraryElements.JAR))
            attribute(TargetJvmVersion.TARGET_JVM_VERSION_ATTRIBUTE, 17)
        }
        outgoing.artifact(tasks["shadowJar"])
    }
    "runtimeElements" {
        attributes {
            attribute(Usage.USAGE_ATTRIBUTE, project.objects.named(Usage.JAVA_RUNTIME))
            attribute(Category.CATEGORY_ATTRIBUTE, project.objects.named(Category.LIBRARY))
            attribute(Bundling.BUNDLING_ATTRIBUTE, project.objects.named(Bundling.SHADOWED))
            attribute(LibraryElements.LIBRARY_ELEMENTS_ATTRIBUTE, project.objects.named(LibraryElements.JAR))
            attribute(TargetJvmVersion.TARGET_JVM_VERSION_ATTRIBUTE, 17)
        }
        outgoing.artifact(tasks["shadowJar"])
    }
    "mainSourceElements" {
        attributes {
            attribute(Usage.USAGE_ATTRIBUTE, project.objects.named(Usage.JAVA_RUNTIME))
            attribute(Category.CATEGORY_ATTRIBUTE, project.objects.named(Category.DOCUMENTATION))
            attribute(Bundling.BUNDLING_ATTRIBUTE, project.objects.named(Bundling.SHADOWED))
            attribute(DocsType.DOCS_TYPE_ATTRIBUTE, project.objects.named(DocsType.SOURCES))
        }
        outgoing.artifact(tasks.named("sourcesJar"))
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = "melodymine"
            shadow.component(this)
            artifact(tasks["sourcesJar"])
            version = "${project.version}-SNAPSHOT"
            setPom(this)
        }
    }

    repositories {
        maven {
            name = "sayandevelopment-repo"
            url = uri("https://repo.sayandev.org/snapshots/")

            credentials {
                username = System.getenv("REPO_SAYAN_USER") ?: project.findProperty("repo.sayan.user") as String
                password = System.getenv("REPO_SAYAN_TOKEN") ?: project.findProperty("repo.sayan.token") as String
            }
        }
    }
}

fun setPom(publication: MavenPublication) {
    publication.pom {
        name.set("melodymine")
        description.set(rootProject.description)
        url.set("https://github.com/vallerian/melodymine")
        licenses {
            license {
                name.set("Apache License 2.0")
                url.set("https://github.com/Vallerian/MelodyMine/blob/master/LICENSE")
            }
        }
        developers {
            developer {
                id.set("taher7")
                name.set("taher moradi")
                email.set("")
            }
        }
        scm {
            connection.set("scm:git:github.com/vallerian/melodymine.git")
            developerConnection.set("scm:git:ssh://github.com/valleryan/melodymine.git")
            url.set("https://github.com/vallerian/melodymine/tree/master")
        }
    }
}