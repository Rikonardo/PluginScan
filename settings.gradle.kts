pluginManagement {
    val kspVersion: String by settings
    plugins {
        id("com.google.devtools.ksp") version kspVersion apply false
    }
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

rootProject.name = "PluginScan"
include("platform-jvm")
include("preprocessor")
include("platform-web")
