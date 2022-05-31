package com.rikonardo.pluginscan.checks

import com.rikonardo.cafebabe.ClassFile
import com.rikonardo.pluginscan.checks.utils.*
import com.rikonardo.pluginscan.framework.annotations.RegisterCheck
import com.rikonardo.pluginscan.framework.types.Check
import com.rikonardo.pluginscan.framework.types.ReportEntry
import com.rikonardo.pluginscan.framework.types.RiskLevel

@RegisterCheck
class ModuleInfoWithSuper : Check() {
    override fun processClass(classFile: ClassFile, fileName: String) {
        if (classFile.data.superClass > 0 && fileName.substringAfterLast("/").substringBeforeLast(".") == "module-info") {
            report(
                RiskLevel.HIGH,
                "Probably malicious module-info file",
                "Found module-info class with super class",
                listOf(ReportEntry.InClass(className(fileName)))
            )
        }
    }
}
