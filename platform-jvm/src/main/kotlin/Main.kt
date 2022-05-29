import com.github.ajalt.mordant.rendering.TextColors.*
import com.github.ajalt.mordant.rendering.TextStyles.*
import com.rikonardo.pluginscan.framework.PluginScan
import com.rikonardo.pluginscan.framework.jar.JarEntry
import com.rikonardo.pluginscan.framework.jar.JarFile
import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.default
import java.io.File
import java.io.PrintStream
import java.util.*
import java.util.zip.ZipEntry
import java.util.zip.ZipFile

fun main(args: Array<String>) {
    val parser = ArgParser("java -jar <jar-file>")
    val input by parser.argument(ArgType.String, fullName = "input", description = "Input file or directory")
    val debug by parser.option(ArgType.Boolean, fullName = "show-errors", description = "Show scan errors").default(false)
    parser.parse(args)
    println(bold(brightWhite("PluginScan ${brightCyan("v${PluginScan.VERSION}")}")))
    println(brightWhite("By Rikonardo & contributors: ${brightCyan("https://github.com/Rikonardo/PluginScan")}"))
    println()

    val file = File(input)
    if (!file.exists()) {
        println(brightRed("File or directory \"${brightCyan(file.canonicalPath)}\" does not exist"))
        return
    }
    if (file.isDirectory) {
        val files = file.listFiles()
        if (files == null) {
            println(brightRed("Directory \"${brightCyan(file.canonicalPath)}\" is not readable"))
            return
        }
        for (f in files) {
            if (f.isFile) {
                println(brightWhite("Processing file \"${brightCyan(f.canonicalPath)}\""))
                processFile(f, traceErrors = debug)
            }
        }
    } else {
        println(brightWhite("Processing file \"${brightCyan(file.canonicalPath)}\""))
        processFile(file, traceErrors = debug)
    }
}

fun processFile(file: File, traceErrors: Boolean = false) {
    try {
        val entries = mutableListOf<JarEntry>()
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
        if (result.reports.isEmpty()) {
            println(white("Nothing found"))
            println()
            return
        }
        result.reports.forEach { report ->
            printReport(report)
        }
        if (traceErrors) {
            result.errors.forEach { error ->
                printError(error)
            }
        } else if (result.errors.isNotEmpty()) {
            println(brightRed("${result.errors.size} errors happened during scanning"))
            println(brightRed("Launch with ${white("--show-errors")} flag to see details"))
            println()
        }
    } catch (excetion: Exception) {
        println(brightRed("Error processing file \"${brightCyan(file.canonicalPath)}\""))
        if (traceErrors)
            excetion.printStackTrace()
        else
            println(brightRed("Launch with ${white("--show-errors")} flag to see details"))
        println()
    }
}
