package com.rikonardo.pluginscan.framework

import com.rikonardo.cafebabe.ClassFile
import com.rikonardo.pluginscan.framework.internal.ParsingFailedCheck
import com.rikonardo.pluginscan.framework.jar.JarFile
import com.rikonardo.pluginscan.framework.types.*
import com.rikonardo.pluginscan.preprocessor.generated.checksList

object PluginScan {
    const val VERSION = "1.0.1"
    fun scan(jar: JarFile, sortOutput: Boolean = true, groupOutput: Boolean = false): ScanResult {
        val checks = checksList()
        val reportData = mutableListOf<CheckReport>()
        val errors = mutableListOf<ScanError>()
        val parsingFailedCheck = ParsingFailedCheck()

        for (check in checks) {
            val report: (
                risk: RiskLevel,
                message: String,
                description: String,
                entries: List<ReportEntry>
            ) -> Unit = { risk, message, description, entries ->
                reportData.add(CheckReport(risk, message, description, entries, check))
            }
            check.jar = jar
            check.report = report
            try { check.before() } catch (e: Exception) { errors.add(ScanError(check, e, ScanError.Step.BEFORE)) }
        }
        val classes = jar.files.filter { it.path.endsWith(".class") }.mapNotNull {
            try {
                ClassFile(it.content) to it.path
            } catch (e: Exception) {
                reportData.add(
                    CheckReport(
                        RiskLevel.HIGH,
                        "Possible heavy obfuscation",
                        "Failed to parse class file",
                        listOf(
                            ReportEntry.InClass(it.path.substringBeforeLast(".").replace("/", "."))
                        ),
                        parsingFailedCheck
                    )
                )
                null
            }
        }
        for (classEntry in classes) {
            for (check in checks) {
                if (errors.any { it.check == check && it.step == ScanError.Step.BEFORE }) continue
                try {
                    check.processClass(classEntry.first, classEntry.second)
                } catch (e: Exception) {
                    errors.add(ScanError(check, e, ScanError.Step.PROCESS_CLASS, classEntry.second))
                }
            }
        }
        for (check in checks) {
            if (errors.any { it.check == check && it.step == ScanError.Step.BEFORE }) continue
            try { check.after() } catch (e: Exception) { errors.add(ScanError(check, e, ScanError.Step.AFTER)) }
        }
        if (sortOutput) {
            reportData.sortBy { -it.risk.ordinal }
        }
        if (groupOutput) {
            val grouped = mutableListOf<MutableList<CheckReport>>()
            for (report in reportData) {
                val existed = grouped.find {
                    it[0].providedBy == report.providedBy &&
                    it[0].risk == report.risk &&
                    it[0].message == report.message &&
                    it[0].description == report.description
                }
                if (existed != null) {
                    existed.add(report)
                } else {
                    grouped.add(mutableListOf(report))
                }
            }
            reportData.clear()
            for (group in grouped) {
                reportData.add(CheckReport(
                    group[0].risk,
                    group[0].message,
                    group[0].description,
                    group.map { it.entries }.flatten(),
                    group[0].providedBy
                ))
            }
        }
        return ScanResult(reportData.toList(), errors.toList())
    }
}
