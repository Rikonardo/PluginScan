package com.rikonardo.pluginscan.checks

import com.rikonardo.cafebabe.ClassFile
import com.rikonardo.pluginscan.framework.annotations.RegisterCheck
import com.rikonardo.pluginscan.framework.types.Check
import com.rikonardo.pluginscan.framework.types.ReportEntry
import com.rikonardo.pluginscan.framework.types.RiskLevel

@RegisterCheck
class BytecodeManipulationLibraries : Check() {
    val libraries = listOf(
        "net/bytebuddy",
        "org/objectweb/asm",
        "javassist",
    ).map { it.split("/") }

    val found = mutableListOf<String>()

    override fun processClass(classFile: ClassFile, fileName: String) {
        val libFound = libraries.find { it.isSubsetOf(fileName.split("/")) } ?: return
        val packageName = fileName.substringBeforeLast(
            libFound.joinToString("/")
        ) + libFound.joinToString("/")
        if (!found.contains(packageName))
            found.add(packageName)
    }

    override fun after() {
        if (found.isEmpty()) return
        report(
            RiskLevel.HIGH,
            "This plugin can edit compiled java classes",
            "Bytecode manipulation libraries were found in classpath. Possible self-spreading worm",
            found.map { ReportEntry.InAny(it.replace("/", ".") + ".*") }
        )
    }
}

private fun List<*>.isSubsetOf(other: List<*>): Boolean {
    var i = 0
    var j: Int
    while (i < this.size) {
        j = 0
        while (j < other.size) {
            if (this[i] == other[j]) break
            j++
        }
        if (j == other.size) return false
        i++
    }

    return true
}
