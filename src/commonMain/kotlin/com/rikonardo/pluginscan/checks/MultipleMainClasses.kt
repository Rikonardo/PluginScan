package com.rikonardo.pluginscan.checks

import com.rikonardo.cafebabe.ClassFile
import com.rikonardo.pluginscan.checks.utils.*
import com.rikonardo.pluginscan.framework.annotations.RegisterCheck
import com.rikonardo.pluginscan.framework.types.Check
import com.rikonardo.pluginscan.framework.types.ReportEntry
import com.rikonardo.pluginscan.framework.types.RiskLevel

@RegisterCheck
class MultipleMainClasses : Check() {
    val mainClasses = mapOf(
        "org/bukkit/plugin/java/JavaPlugin" to "Bukkit",
        "net/md_5/bungee/api/plugin/JavaPlugin" to "BungeeCord",
    )
    val lists = mainClasses.map { it.key to mutableListOf<String>() }.toMap().toMutableMap()

    override fun processClass(classFile: ClassFile, fileName: String) {
        if (classFile.data.superClass < 1) return // module-info.class files have no superclass
        if (lists.containsKey(classFile.parent)) {
            lists[classFile.parent]!!.add(fileName)
        }
    }

    override fun after() {
        for ((parent, fileNames) in lists.entries) {
            if (fileNames.size >= 2) {
                report(
                    RiskLevel.MODERATE,
                    "Multiple main classes, possible proxy-class injection",
                    "Multiple main classes for platform ${mainClasses[parent]} found",
                    fileNames.map { ReportEntry.InClass(className(it)) }
                )
            }
        }
    }
}
