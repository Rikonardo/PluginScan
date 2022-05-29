package com.rikonardo.pluginscan.checks

import com.rikonardo.cafebabe.ClassFile
import com.rikonardo.pluginscan.checks.utils.*
import com.rikonardo.pluginscan.framework.annotations.RegisterCheck
import com.rikonardo.pluginscan.framework.types.Check
import com.rikonardo.pluginscan.framework.types.ReportEntry
import com.rikonardo.pluginscan.framework.types.RiskLevel

@RegisterCheck
class PermissionAttachments : Check() {
    override fun processClass(classFile: ClassFile, fileName: String) {
        if (
            classFile.doReferenceClass("org/bukkit/permissions/PermissionAttachment") ||
            classFile.doReferencedInArgTypes("org/bukkit/permissions/PermissionAttachment") ||
            classFile.doReferencedInReturnTypes("org/bukkit/permissions/PermissionAttachment")
        ) report(
            RiskLevel.MODERATE,
            "This plugin may be able to spoof player permissions",
            "PermissionAttachments api reference found",
            listOf(ReportEntry.In(className(fileName)))
        )
    }
}
