package com.rikonardo.pluginscan.checks

import com.rikonardo.cafebabe.ClassFile
import com.rikonardo.pluginscan.checks.utils.*
import com.rikonardo.pluginscan.framework.annotations.RegisterCheck
import com.rikonardo.pluginscan.framework.types.Check
import com.rikonardo.pluginscan.framework.types.ReportEntry
import com.rikonardo.pluginscan.framework.types.RiskLevel

@RegisterCheck
class BukkitLoadPlugin : Check() {
    override fun processClass(classFile: ClassFile, fileName: String) {
        if (
            classFile.doReferenceMethod("org/bukkit/plugin/PluginManager", "loadPlugin") ||
            classFile.doReferenceMethod("org/bukkit/plugin/PluginManager", "loadPlugins")
        ) report(
            RiskLevel.MODERATE,
            "Plugin can load new plugins in runtime",
            "Found loadPlugin method reference",
            listOf(ReportEntry.InClass(className(fileName)))
        )
    }
}
