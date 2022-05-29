package com.rikonardo.pluginscan.checks

import com.rikonardo.cafebabe.ClassFile
import com.rikonardo.pluginscan.checks.utils.*
import com.rikonardo.pluginscan.framework.annotations.RegisterCheck
import com.rikonardo.pluginscan.framework.types.Check
import com.rikonardo.pluginscan.framework.types.ReportEntry
import com.rikonardo.pluginscan.framework.types.RiskLevel

@RegisterCheck
class RuntimeExec : Check() {
    override fun processClass(classFile: ClassFile, fileName: String) {
        if (classFile.doReferenceMethod("java/lang/Runtime", "exec")) report(
            RiskLevel.CRITICAL,
            "Plugin can execute system commands, probably backdoor",
            "Plugin class references Runtime.exec()",
            listOf(ReportEntry.In(className(fileName)))
        )
    }
}
