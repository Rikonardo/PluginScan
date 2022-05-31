package com.rikonardo.pluginscan.checks

import com.rikonardo.cafebabe.ClassFile
import com.rikonardo.pluginscan.checks.utils.*
import com.rikonardo.pluginscan.framework.annotations.RegisterCheck
import com.rikonardo.pluginscan.framework.types.Check
import com.rikonardo.pluginscan.framework.types.ReportEntry
import com.rikonardo.pluginscan.framework.types.RiskLevel

@RegisterCheck
class ConsoleCommands : Check() {
    override fun processClass(classFile: ClassFile, fileName: String) {
        if (
            classFile.doReferenceMethod("org/bukkit/Server", "dispatchCommand") &&
            (
                classFile.doReferenceMethod("org/bukkit/Server", "getConsoleSender") ||
                classFile.doReferenceClass("org/bukkit/command/ConsoleCommandSender")
            )
        ) report(
            RiskLevel.MODERATE,
            "This plugin may be able to execute commands as the console",
            "ConsoleCommandSender and dispatchCommand method referenced in the same class",
            listOf(ReportEntry.InClass(className(fileName)))
        )
    }
}
