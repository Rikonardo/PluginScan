<div align="center"><h1>PluginScan - Minecraft plugin anti-malware scanner</h1></div>

<div align="center"><img alt="Logo" src="logo.png"/></div>

<div align="center">
    <a href="https://github.com/Rikonardo/PluginScan/issues"><img alt="Open issues" src="https://img.shields.io/github/issues-raw/Rikonardo/PluginScan"/></a>
    <a href="https://github.com/Rikonardo/PluginScan/releases/latest"><img alt="GitHub downloads" src="https://img.shields.io/github/downloads/Rikonardo/PluginScan/total"></a>
    <img alt="Version" src="https://img.shields.io/maven-metadata/v?metadataUrl=https%3A%2F%2Fmaven.rikonardo.com%2Freleases%2Fcom%2Frikonardo%2Fpluginscan%2FPluginScan%2Fmaven-metadata.xml"/>
    <a href="https://www.codefactor.io/repository/github/rikonardo/pluginscan"><img alt="CodeFactor" src="https://www.codefactor.io/repository/github/rikonardo/pluginscan/badge"/></a>
    <a href="https://www.codefactor.io/repository/github/rikonardo/pluginscan"><img alt="Discord" src="https://img.shields.io/discord/982967258013896734?color=%237289DA&label=discord&logo=discord&logoColor=%237289DA"></a>
</div>

<br>

<hr>

**PluginScan** is a cross-platform java executable analyzer designed to detect malware and other malicious code in Minecraft plugins. It uses Kotlin multiplatform [CafeBabe](https://github.com/Rikonardo/CafeBabe) library to analyze class metadata and detect suspicious patterns.

**❗ Please note that this is not some magic tool that recognizes any malicious code. It recognizes code patterns that can theoretically be used by malicious code, but can also be used for completely legitimate purposes. Also note that the absence of detections of malicious code by the scanner does not guarantee the safety of the plugin, as it can be deceived by complex obfuscation.**

Currently, PluginScan can be used as **[website](#pluginscan-web)** and **[CLI tool](#pluginscan-cli)**.

## PluginScan Web
Web interface is available on [https://scan.rikonardo.com](https://scan.rikonardo.com).

It is created using Kotlin/JS with React and can be used offline. But we strongly recommend to use a CLI, because it is much faster and more reliable.

## PluginScan CLI
PluginScan CLI tool is a JVM-powered release of PluginScan. Unlike web version, it allows you to scan multiple plugins at once by passing directory to input. It is also has better way of tracking errors during scanning if they happen.

This tool requires Java 8+ to run and can be downloaded from [releases page](https://github.com/Rikonardo/PluginScan/releases/latest).

Usage:

```sh
java -jar PluginScan.jar <input-file-or-directory>
```

## Usage as library
PluginScan also is a Kotlin library that can be used as a dependency in your project. You can install it from maven repository:

```kotlin
repositories {
    maven {
        url = uri("https://maven.rikonardo.com/releases")
    }
}

dependencies {
    implementation("com.rikonardo.pluginscan:PluginScan:1.0.3")
}
```

To scan jar file, you can use `PluginScan.scan` method. It takes JarFile object and two optional arguments (`sortOutput` and `groupOutput`).

Note that you will need to read jar file by yourself, because currently there is no multiplatform Kotlin zip library. JarFile class is just a wrapper around JarFile list. JarFile is a container for file name inside jar, and it's ByteArray content. You must pass all files from jar, not only classes. Here is an example of PluginScan usage in Kotlin/JVM:

```kotlin
fun main() {
    val entries = mutableListOf<JarEntry>()
    val file = File("plugin.jar")
    ZipFile(file.canonicalPath).use { zipFile ->
        val zipEntries: Enumeration<*> = zipFile.entries()
        while (zipEntries.hasMoreElements()) {
            val zipEntry = zipEntries.nextElement() as ZipEntry
            if (zipEntry.isDirectory) continue
            val fileName: String = zipEntry.name
            entries.add(JarEntry(fileName, zipFile.getInputStream(zipEntry).readBytes()))
        }
    }
    val result = PluginScan.scan(JarFile(entries), groupOutput = true)
    println(result.reports.size)
}
```

Notice that PluginScan relies on file path inside jar, so you need to make sure you pass it in the correct format. Here is an examples of correct path strings:

```
META-INF/LICENSE.txt
plugin.yml
com/example/plugin/ExamplePlugin.class
```

## Contributing
This project was originally meant as a platform, that will be grown by community, so we always welcome any contribution.

You can easily add your own check by creating class from template below and put it into `com.rikonardo.pluginscan.checks` package.

```kotlin
@RegisterCheck
class MyCheck : Check() {

}
```

Check class has 3 overridable methods:

```kotlin
@RegisterCheck
class MyCheck : Check() {
    override fun before() { }
    override fun processClass(classFile: ClassFile, fileName: String) { }
    override fun after() { }
}
```

Instance of `Check` class is created for each scan session, `before()` method is called before scan, `processClass()` is called for each class in jar, and `after()` method is called after scan. You can also access all files inside jar at any scan step by accessing `jar` field of `Check` class.

To report suspicious code, you can use `report()` method.

Here is an simple check, that reports reference to `Player.setOp()` method:

```kotlin
@RegisterCheck
class SetOp : Check() {
    override fun processClass(classFile: ClassFile, fileName: String) {
        if (
            classFile.doReferenceMethod("org/bukkit/entity/Player", "setOp")
        ) report(
            RiskLevel.MODERATE,
            "Plugin can set player's op status",
            "Found setOp method reference",
            listOf(ReportEntry.In(className(fileName)))
        )
    }
}
```

Some useful code, like `className()` which transforms class file name inside jar into java-like class name, `ClassFile.doReferenceMethod()` which checks if class file references given method, and many other useful methods are located in `com.rikonardo.pluginscan.checks.utils` package.

Now just fork this project and start coding! Then create pull request and we will merge it into master.
