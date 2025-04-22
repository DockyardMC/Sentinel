plugins {
    kotlin("jvm") version "2.1.20"
}

group = "io.github.dockyardmc.sentinel"
version = "1.0"

subprojects {
    apply(plugin = "kotlin")

    repositories {
        mavenCentral()
        maven("https://jitpack.io")
        maven("https://mvn.devos.one/releases")
        maven("https://mvn.devos.one/snapshots")
    }

    kotlin {
        jvmToolchain(21)
    }

    java {
        toolchain.languageVersion.set(JavaLanguageVersion.of(21))
    }
}


dependencies {
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}