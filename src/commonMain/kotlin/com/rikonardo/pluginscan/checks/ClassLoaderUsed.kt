package com.rikonardo.pluginscan.checks

import com.rikonardo.cafebabe.ClassFile
import com.rikonardo.pluginscan.checks.utils.*
import com.rikonardo.pluginscan.framework.annotations.RegisterCheck
import com.rikonardo.pluginscan.framework.types.Check
import com.rikonardo.pluginscan.framework.types.ReportEntry
import com.rikonardo.pluginscan.framework.types.RiskLevel

@RegisterCheck
class ClassLoaderUsed : Check() {
    override fun processClass(classFile: ClassFile, fileName: String) {
        if (
            classFile.doReferenceClass("java/lang/ClassLoader") ||
            classFile.doReferenceClass("java/lang/URLClassLoader")
        ) report(
            RiskLevel.HIGH,
            "Plugin may be able to load arbitrary java code in runtime",
            "Found reference to ClassLoader or URLClassLoader",
            listOf(ReportEntry.InClass(className(fileName)))
        )
    }
}
