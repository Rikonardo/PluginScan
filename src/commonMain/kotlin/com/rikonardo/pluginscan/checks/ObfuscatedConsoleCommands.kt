package com.rikonardo.pluginscan.checks

import com.rikonardo.cafebabe.ClassFile
import com.rikonardo.pluginscan.checks.utils.*
import com.rikonardo.pluginscan.framework.annotations.RegisterCheck
import com.rikonardo.pluginscan.framework.types.Check
import com.rikonardo.pluginscan.framework.types.ReportEntry
import com.rikonardo.pluginscan.framework.types.RiskLevel

@RegisterCheck
class ObfuscatedConsoleCommands : Check() {
    override fun processClass(classFile: ClassFile, fileName: String) {
        if (
            !classFile.doReferenceClass("org/bukkit/command/ConsoleCommandSender") &&
            (
                classFile.doReferencedInReturnTypes("org/bukkit/command/ConsoleCommandSender") ||
                classFile.doReferencedInArgTypes("org/bukkit/command/ConsoleCommandSender")
            ) && (
                classFile.doReferencedInArgTypes("org/bukkit/command/CommandSender") ||
                classFile.doReferencedInArgTypes("org/bukkit/command/ConsoleCommandSender")
            )
        ) report(
            RiskLevel.LOW,
            "Possible obfuscation of console command execution",
            "ConsoleCommandSender referenced in method args or return type but not in class references",
            listOf(ReportEntry.In(className(fileName)))
        )
    }
}
