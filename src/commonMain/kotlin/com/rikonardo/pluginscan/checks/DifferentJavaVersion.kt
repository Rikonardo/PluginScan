package com.rikonardo.pluginscan.checks

import com.rikonardo.cafebabe.ClassFile
import com.rikonardo.pluginscan.checks.utils.*
import com.rikonardo.pluginscan.framework.annotations.RegisterCheck
import com.rikonardo.pluginscan.framework.types.Check
import com.rikonardo.pluginscan.framework.types.ReportEntry
import com.rikonardo.pluginscan.framework.types.RiskLevel

@RegisterCheck
class DifferentJavaVersion : Check() {
    val lists = mutableMapOf<Pair<Int, Int>, MutableList<String>>()

    override fun processClass(classFile: ClassFile, fileName: String) {
        if (classFile.data.superClass < 1) return // module-info.class files have no superclass
        if (!lists.containsKey(classFile.version.major to classFile.version.minor)) {
            lists[classFile.version.major to classFile.version.minor] = mutableListOf(fileName)
        } else {
            lists[classFile.version.major to classFile.version.minor]!!.add(fileName)
        }
    }

    override fun after() {
        if (!lists.any { it.value.size >= 10 }) return
        for ((version, files) in lists.entries.filter { it.value.size < 10 }) {
            report(
                RiskLevel.LOW,
                "Possible class injection (or just a library)",
                "Small group of classes (less than 10) have different class version (${version.first}.${version.second})",
                files.map { ReportEntry.In(className(it)) }
            )
        }
    }
}
