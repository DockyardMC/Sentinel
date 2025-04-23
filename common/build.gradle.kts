plugins {
    kotlin("jvm")
}

group = "io.github.dockyardmc.sentinel.common"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("cz.lukynka:hollow:1.0")
    implementation("io.github.dockyardmc:tide:1.3")
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