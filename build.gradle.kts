plugins {
    id("org.jetbrains.kotlin.jvm") version "1.6.10"
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("application")
}

group = "com.balduvian"
version = "0.2"

val lwjglVersion = "3.3.1"
val jomlVersion = "1.10.4"

repositories {
    mavenCentral()
    maven(url = "https://oss.sonatype.org/content/repositories/snapshots/")
}

dependencies {
    implementation("org.lwjgl:lwjgl:${lwjglVersion}")
    implementation("org.lwjgl:lwjgl-opengl:${lwjglVersion}")
    implementation("org.lwjgl:lwjgl-glfw:${lwjglVersion}")
    implementation("org.lwjgl:lwjgl-openal:${lwjglVersion}")

    implementation("org.lwjgl:lwjgl:${lwjglVersion}:natives-windows")
    implementation("org.lwjgl:lwjgl-opengl:${lwjglVersion}:natives-windows")
    implementation("org.lwjgl:lwjgl-glfw:${lwjglVersion}:natives-windows")
    implementation("org.lwjgl:lwjgl-openal:${lwjglVersion}:natives-windows")

    implementation("org.joml:joml:${jomlVersion}")
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "17"
        kotlinOptions.apiVersion = "1.6"
    }
    shadowJar {
        archiveFileName.set("${project.name}.jar")
        manifest {
            attributes(mapOf("Main-Class" to "MainKt"))
        }
    }
    jar {
        archiveFileName.set("${project.name}.jar")
        manifest {
            attributes(mapOf("Main-Class" to "MainKt"))
        }
    }
    build {
        dependsOn(shadowJar)
    }
}

application {
    mainClass.set("MainKt")
}
