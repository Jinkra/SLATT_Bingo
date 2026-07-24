import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import io.izzel.taboolib.gradle.*
import org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_1_8
import io.izzel.taboolib.gradle.Basic
import io.izzel.taboolib.gradle.Kether
import io.izzel.taboolib.gradle.Bukkit
import io.izzel.taboolib.gradle.I18n
import io.izzel.taboolib.gradle.CommandHelper
import io.izzel.taboolib.gradle.BukkitNMS
import io.izzel.taboolib.gradle.BukkitNMSUtil
import io.izzel.taboolib.gradle.BukkitUtil


plugins {
    java
    id("io.izzel.taboolib") version "2.0.38"
    id("org.jetbrains.kotlin.jvm") version "2.2.0"
}

taboolib {
    env {
        install(Basic)
        install(Kether)
        install(Bukkit)
        install(I18n)
        install(CommandHelper)
        install(BukkitHook)
        install(BukkitNMS)
        install(BukkitNMSUtil)
        install(BukkitUtil)
    }
    description {
        name = "Bingo"
        desc("Yet Another Bingo")
        contributors {
            name("Happy")
        }
        dependencies {
            name("TAB")
            name("Chunky")
        }
    }
    version { taboolib = "6.3.0-c6f096d" }
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.codemc.io/repository/maven-public/")
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://jitpack.io")
    maven("https://repo.tabooproject.org/repository/releases")
}

dependencies {
    compileOnly("ink.ptms.core:v12004:12004:mapped")
    compileOnly("ink.ptms.core:v12004:12004:universal")
    compileOnly(kotlin("stdlib"))
    compileOnly(fileTree("libs"))

    compileOnly("org.popcraft:chunky-common:1.3.38")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.withType<KotlinCompile> {
    compilerOptions {
        jvmTarget.set(JVM_1_8)
        freeCompilerArgs.add("-Xjvm-default=all")
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}
