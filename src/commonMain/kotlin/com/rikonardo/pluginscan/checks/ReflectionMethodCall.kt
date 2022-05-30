package com.rikonardo.pluginscan.checks

import com.rikonardo.cafebabe.ClassFile
import com.rikonardo.pluginscan.checks.utils.*
import com.rikonardo.pluginscan.framework.annotations.RegisterCheck
import com.rikonardo.pluginscan.framework.types.Check
import com.rikonardo.pluginscan.framework.types.ReportEntry
import com.rikonardo.pluginscan.framework.types.RiskLevel

@RegisterCheck
class ReflectionMethodCall : Check() {
    override fun processClass(classFile: ClassFile, fileName: String) {
        if (classFile.doReferenceMethod("java/lang/reflect/Method", "invoke")) report(
            RiskLevel.LOW,
            "Plugin can hide malicious references by using reflection",
            "Plugin references reflection method invoke API",
            listOf(ReportEntry.InClass(className(fileName)))
        )
    }
}
