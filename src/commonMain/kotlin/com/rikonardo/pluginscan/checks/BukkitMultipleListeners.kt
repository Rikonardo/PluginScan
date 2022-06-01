package com.rikonardo.pluginscan.checks

import com.rikonardo.cafebabe.ClassFile
import com.rikonardo.pluginscan.checks.utils.*
import com.rikonardo.pluginscan.framework.annotations.RegisterCheck
import com.rikonardo.pluginscan.framework.types.Check
import com.rikonardo.pluginscan.framework.types.ReportEntry
import com.rikonardo.pluginscan.framework.types.RiskLevel

@RegisterCheck
class BukkitMultipleListeners : Check() {
    val listeners = mutableListOf<String>()

    override fun processClass(classFile: ClassFile, fileName: String) {
        if (classFile.interfaces.any { it.name == "org/bukkit/event/Listener" }) {
            listeners.add(classFile.name)
        }
    }

    override fun after() {
        if (listeners.size < 2) return
        report(
            RiskLevel.LOW,
            "Multiple listeners found",
            "Found multiple classes implementing the Listener interface, possible injected listener",
            listeners.map { ReportEntry.InClass(className(it)) }
        )
    }
}
