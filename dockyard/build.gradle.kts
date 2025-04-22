plugins {
    kotlin("jvm")
}

group = "io.github.dockyardmc.sentinel.dockyard"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.github.dockyardmc:dockyard:0.9.3")
    implementation(project(":common"))
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}