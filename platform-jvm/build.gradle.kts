plugins {
    kotlin("jvm")
    application
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = rootProject.group
version = rootProject.version

repositories {
    mavenCentral()
    maven {
        url = uri("https://maven.rikonardo.com/releases")
    }
}

dependencies {
    testImplementation(kotlin("test"))
    implementation(rootProject)
    implementation("com.github.ajalt.mordant:mordant:2.0.0-beta6")
    implementation("org.jetbrains.kotlinx:kotlinx-cli:0.3.4")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(8))
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

tasks.test {
    useJUnitPlatform()
}

tasks {
    named<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowJar") {
        archiveClassifier.set("")
    }

    build {
        dependsOn(shadowJar)
    }
}

application {
    mainClass.set("MainKt")
}
