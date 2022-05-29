package com.rikonardo.pluginscan.checks

import com.rikonardo.cafebabe.ClassFile
import com.rikonardo.cafebabe.data.constantpool.ConstantUtf8
import com.rikonardo.cafebabe.data.numbers.BinaryInt
import com.rikonardo.pluginscan.checks.utils.*
import com.rikonardo.pluginscan.framework.annotations.RegisterCheck
import com.rikonardo.pluginscan.framework.types.Check
import com.rikonardo.pluginscan.framework.types.ReportEntry
import com.rikonardo.pluginscan.framework.types.RiskLevel

@RegisterCheck
class JavaSourceName : Check() {
    val list = mutableListOf<String>()

    override fun processClass(classFile: ClassFile, fileName: String) {
        val sourceNameAttr = classFile.attributes.find { it.name == "SourceFile" } ?: return
        val sourceName = classFile.constantPool[BinaryInt.from(sourceNameAttr.info).value] as ConstantUtf8
        if (!sourceName.value.endsWith(".java")) return
        val sourceClassFileName = sourceName.value.substringBeforeLast(".")
        if (sourceClassFileName.trim().isEmpty()) return
        val className = classFile.name.substringAfterLast('/')
        if (!className.contains(sourceClassFileName)) list.add(fileName)
    }

    override fun after() {
        if (list.size >= 10 || list.isEmpty()) return
        report(
            RiskLevel.LOW,
            "Possible injection or obfuscation",
            "Small group of classes (less than 10) have source file names that does not match class names",
            list.map { ReportEntry.In(className(it)) }
        )
    }
}
