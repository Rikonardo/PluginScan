package com.rikonardo.pluginscan.checks

import com.rikonardo.cafebabe.ClassFile
import com.rikonardo.pluginscan.checks.utils.*
import com.rikonardo.pluginscan.framework.annotations.RegisterCheck
import com.rikonardo.pluginscan.framework.types.Check
import com.rikonardo.pluginscan.framework.types.ReportEntry
import com.rikonardo.pluginscan.framework.types.RiskLevel

@RegisterCheck
class SetOp : Check() {
    override fun processClass(classFile: ClassFile, fileName: String) {
        if (
            classFile.doReferenceMethod("org/bukkit/entity/Player", "setOp") ||
            classFile.doReferenceMethod("org/bukkit/command/CommandSender", "setOp")
        ) report(
            RiskLevel.MODERATE,
            "Plugin can set player's op status",
            "Found setOp method reference",
            listOf(ReportEntry.In(className(fileName)))
        )
    }
}
