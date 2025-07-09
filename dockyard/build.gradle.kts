plugins {
    id("io.realm.kotlin") version "3.0.0"
    kotlin("jvm")
}

group = "io.github.dockyardmc.sentinel.dockyard"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("cz.lukynka:hollow-realm:1.2")
    implementation("io.github.dockyardmc:dockyard:0.10.2")
    implementation(project(":common"))
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}