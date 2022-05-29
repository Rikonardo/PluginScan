package com.rikonardo.pluginscan.framework.types

import com.rikonardo.cafebabe.ClassFile
import com.rikonardo.pluginscan.framework.jar.JarFile

abstract class Check {
    lateinit var report: ((
        risk: RiskLevel,
        message: String,
        description: String,
        entries: List<ReportEntry>
    ) -> Unit)
    lateinit var jar: JarFile

    open fun before() {}
    open fun processClass(classFile: ClassFile, fileName: String) {}
    open fun after() {}
}
