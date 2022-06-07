package com.rikonardo.pluginscan.checks

import com.rikonardo.pluginscan.checks.utils.*
import com.rikonardo.pluginscan.framework.annotations.RegisterCheck
import com.rikonardo.pluginscan.framework.types.Check
import com.rikonardo.pluginscan.framework.types.ReportEntry
import com.rikonardo.pluginscan.framework.types.RiskLevel

@RegisterCheck
class ClassTooLarge : Check() {
    private val maxClassSize = 10000
    override fun before() {
        jar.files.forEach {
            if (it.path.endsWith(".class") && it.content.size > maxClassSize) report(
                RiskLevel.MODERATE,
                "Possible binary embedded in class file",
                "Class weights more then 10KB",
                listOf(ReportEntry.InClass(className(it.path)))
            )
        }
    }
}
