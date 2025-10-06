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
    testImplementation("org.junit.jupiter:junit-jupiter:5.9.1")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("org.assertj:assertj-core:3.27.6")
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