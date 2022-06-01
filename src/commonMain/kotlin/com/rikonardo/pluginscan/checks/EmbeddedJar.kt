package com.rikonardo.pluginscan.checks

import com.rikonardo.pluginscan.framework.annotations.RegisterCheck
import com.rikonardo.pluginscan.framework.types.Check
import com.rikonardo.pluginscan.framework.types.ReportEntry
import com.rikonardo.pluginscan.framework.types.RiskLevel

@RegisterCheck
class EmbeddedJar : Check() {
    override fun before() {
        for (file in jar.files) {
            if (
                file.content.size >= 4 &&
                file.content[0] == 0x50.toByte() &&
                file.content[1] == 0x4b.toByte() &&
                file.content[2] == 0x03.toByte() &&
                file.content[3] == 0x04.toByte()
            ) {
                if (
                    file.path.endsWith(".jar") ||
                    file.path.endsWith(".zip") ||
                    file.path.endsWith(".jarinjar")
                ) {
                    report(
                        RiskLevel.HIGH,
                        "Embedded jar/zip found",
                        "Found embedded file with zip header signature",
                        listOf(ReportEntry.InAny(file.path))
                    )
                } else {
                    report(
                        RiskLevel.CRITICAL,
                        "Hidden embedded jar/zip found",
                        "Found embedded file with zip header signature and mismatched extension",
                        listOf(ReportEntry.InAny(file.path))
                    )
                }
            }
        }
    }
}
