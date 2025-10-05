plugins {
    kotlin("jvm") version "1.9.20"
    application
}

group = "org.itmo"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
    maxHeapSize = "8G"
    minHeapSize = "4G"
}

kotlin {
    jvmToolchain(21)
}

application {
    mainClass.set("MainKt")
}