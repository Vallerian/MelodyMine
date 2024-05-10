import com.github.jengelman.gradle.plugins.shadow.tasks.ConfigureShadowRelocation
import java.net.URL
import java.util.concurrent.Executors

plugins {
    kotlin("jvm") version "1.9.0"
    id("java")
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("org.jetbrains.gradle.plugin.idea-ext") version "1.1.7"
    id("xyz.jpenilla.run-paper") version "2.1.0"
}

group = "ir.taher7.melodymine"
version = "${project.version}"


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

    implementation("com.github.cryptomorin:XSeries:9.7.0") { isTransitive = false }

    implementation("net.kyori:adventure-api:4.16.0")
    implementation("net.kyori:adventure-platform-bukkit:4.3.2")
    implementation("net.kyori:adventure-text-minimessage:4.16.0")


    implementation("com.jeff_media:SpigotUpdateChecker:3.0.3")

    // Socket Io
    implementation("io.socket:socket.io-client:2.1.0")
    implementation("com.github.kenglxn.QRGen:javase:3.0.1")

    implementation("com.zaxxer:HikariCP:4.0.3")

    compileOnly("com.comphenix.protocol:ProtocolLib:5.1.0")
    implementation("org.slf4j:slf4j-api:1.7.25")
    implementation("ch.qos.logback:logback-core:1.2.3")
    implementation("ch.qos.logback:logback-classic:1.2.3")
    implementation("xerces:xercesImpl:2.12.1")


}


val extraDependencies = emptyMap<String, String>()

tasks {
    runServer {
        minecraftVersion("1.20.4")
    }


    val relocate = task<ConfigureShadowRelocation>("relocateShadowJar") {
        target = shadowJar.get()
        prefix = "ir.taher7.melodymine"
    }

    shadowJar {
        dependsOn(relocate)
        exclude("META-INF/**")
        archiveFileName.set("${project.name}-${version}.jar")
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

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

