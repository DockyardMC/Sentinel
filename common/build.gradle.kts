plugins {
    id("io.realm.kotlin") version "3.0.0"
    kotlin("jvm")
}

group = "io.github.dockyardmc.sentinel.common"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("cz.lukynka:hollow-realm:1.2")
    implementation("io.github.dockyardmc:tide:1.6")
    implementation("org.slf4j:slf4j-nop:2.0.9")
    compileOnly("cz.lukynka:pretty-log:1.4")

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}