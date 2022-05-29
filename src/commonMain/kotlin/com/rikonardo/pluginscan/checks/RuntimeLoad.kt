package com.rikonardo.pluginscan.checks

import com.rikonardo.cafebabe.ClassFile
import com.rikonardo.pluginscan.checks.utils.*
import com.rikonardo.pluginscan.framework.annotations.RegisterCheck
import com.rikonardo.pluginscan.framework.types.Check
import com.rikonardo.pluginscan.framework.types.ReportEntry
import com.rikonardo.pluginscan.framework.types.RiskLevel

@RegisterCheck
class RuntimeLoad : Check() {
    override fun processClass(classFile: ClassFile, fileName: String) {
        if (
            classFile.doReferenceMethod("java/lang/Runtime", "load") ||
            classFile.doReferenceMethod("java/lang/Runtime", "loadLibrary")
        ) report(
            RiskLevel.HIGH,
            "Plugin can load native libraries at runtime, possible backdoor dropper",
            "Plugin class references Runtime load or loadLibrary",
            listOf(ReportEntry.In(className(fileName)))
        )
    }
}
