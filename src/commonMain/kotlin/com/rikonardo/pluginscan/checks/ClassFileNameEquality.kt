package com.rikonardo.pluginscan.checks

import com.rikonardo.cafebabe.ClassFile
import com.rikonardo.pluginscan.checks.utils.*
import com.rikonardo.pluginscan.framework.annotations.RegisterCheck
import com.rikonardo.pluginscan.framework.types.Check
import com.rikonardo.pluginscan.framework.types.ReportEntry
import com.rikonardo.pluginscan.framework.types.RiskLevel

@RegisterCheck
class ClassFileNameEquality : Check() {
    override fun processClass(classFile: ClassFile, fileName: String) {
        if (classFile.data.superClass < 1) return // module-info.class files have no superclass
        if (classFile.name.replace("/", ".") != className(fileName)) report(
            RiskLevel.LOW,
            "Broken class (may be result of renaming/injection)",
            "Class name doesn't match its file name",
            listOf(ReportEntry.In(className(fileName)))
        )
    }
}
