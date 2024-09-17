import java.net.URL
import java.util.concurrent.Executors

plugins {
    kotlin("jvm") version "1.9.23"
    id("java-library")
    id("maven-publish")
    id("com.gradleup.shadow") version "8.3.1"
    id("org.jetbrains.gradle.plugin.idea-ext") version "1.1.7"
    id("xyz.jpenilla.run-paper") version "2.1.0"
}

group = "ir.taher7"
version = "${project.version}"
description = "Minecraft voice chat plugin"

repositories {
    mavenLocal()
    mavenCentral()

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

//    PlaceholderAPI
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")


    // Update checker
    maven("https://hub.jeff-media.com/nexus/repository/jeff-media-public/")

    // ProtocolLib
    maven("https://repo.dmulloy2.net/repository/public/")
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.19.4-R0.1-SNAPSHOT")
    compileOnly("me.clip:placeholderapi:2.11.3")

    implementation("org.bstats:bstats-bukkit:3.0.2")

    implementation("com.google.code.gson:gson:2.10.1")

    implementation("com.github.cryptomorin:XSeries:11.2.1")

    implementation("net.kyori:adventure-api:4.17.0")
    implementation("net.kyori:adventure-platform-bukkit:4.3.3")
    implementation("net.kyori:adventure-text-minimessage:4.17.0")


    implementation("com.jeff_media:SpigotUpdateChecker:3.0.3")

    // Socket Io
    implementation("io.socket:socket.io-client:2.1.0")
    implementation("com.github.kenglxn.QRGen:javase:3.0.1")

    implementation("com.zaxxer:HikariCP:4.0.3")

    compileOnly("com.comphenix.protocol:ProtocolLib:5.1.0")
    implementation("org.slf4j:slf4j-api:1.7.25")
    implementation("ch.qos.logback:logback-core:1.4.14")
    implementation("ch.qos.logback:logback-classic:1.4.12")
    implementation("xerces:xercesImpl:2.12.2")
}


val extraDependencies = emptyMap<String, String>()

val relocations = mapOf(
    "net.kyori" to "ir.taher7.melodymine.lib.kyori",
    "kotlin" to "ir.taher7.melodymine.lib.kotlin",
    "com.zaxxer" to "ir.taher7.melodymine.lib.zaxxer",
//    "com.google" to "ir.taher7.melodymine.lib.google",
    "com.cryptomorin" to "ir.taher7.melodymine.lib.cryptomorin",
    "okio" to "ir.taher7.melodymine.lib.okio",
    "okhttp3" to "ir.taher7.melodymine.lib.okhttp3",
    "net.glxn" to "ir.taher7.melodymine.lib.glxn",
    "io.socket" to "ir.taher7.melodymine.lib.socket",
    "ch.qos" to "ir.taher7.melodymine.lib.qos",
    "org.bstats" to "ir.taher7.melodymine.lib.bstats",
    "com.jeff_media" to "ir.taher7.melodymine.lib.jeff_media",
)

java {
    withSourcesJar()
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

tasks {
    runServer {
        minecraftVersion("1.21.1")
    }

    shadowJar {
        exclude("META-INF/**")
        archiveFileName.set("${project.name}-${version}.jar")
        relocations.forEach { (from, to) ->
            relocate(from, to)
        }
        from("LICENSE")
        minimize()
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
        }
    }

    val extraDeps = register("downloadExtraDependencies") {
        val libsDir = File("libs")
        libsDir.mkdirs()
        val ex = Executors.newCachedThreadPool()
        for (entry in extraDependencies) {
            val file = File(libsDir, entry.key)
            if (file.exists())
                continue
            ex.submit {
                println("Downloading ${entry.key} from ${entry.value}")
                URL(entry.value).openStream().use { s -> file.outputStream().use { it.write(s.readBytes()) } }
                println("Successfully downloaded ${entry.key} to ${file.path}")
            }
        }
        ex.shutdown()
        ex.awaitTermination(10, TimeUnit.SECONDS)
    }

    build {
        dependsOn(extraDeps)
        dependsOn(shadowJar)
    }
}

tasks.named<Jar>("sourcesJar") {
    relocations.forEach { (from, to) ->
        val filePattern = Regex("(.*)${from.replace('.', '/')}((?:/|$).*)")
        val textPattern = Regex.fromLiteral(from)
        eachFile {
            filter {
                it.replaceFirst(textPattern, to)
            }
            path = path.replaceFirst(filePattern, "$1${to.replace('.', '/')}$2")
        }
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