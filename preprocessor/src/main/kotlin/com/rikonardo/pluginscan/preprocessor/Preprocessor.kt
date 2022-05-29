package com.rikonardo.pluginscan.preprocessor

import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSNode
import com.google.devtools.ksp.visitor.KSTopDownVisitor
import java.io.OutputStreamWriter

const val annotation = "com.rikonardo.pluginscan.framework.annotations.RegisterCheck"
const val check = "com.rikonardo.pluginscan.framework.types.Check"

class Preprocessor(val codeGenerator: CodeGenerator, val logger: KSPLogger) : SymbolProcessor {
    var invoked = false
    override fun process(resolver: Resolver): List<KSAnnotated> {
        if (invoked)
            return emptyList()
        invoked = true

        val annotated = resolver.getSymbolsWithAnnotation(annotation)
        codeGenerator.createNewFile(
            Dependencies(false),
            "com.rikonardo.pluginscan.preprocessor.generated",
            "ChecksList",
            "kt"
        ).use { output ->
            OutputStreamWriter(output).use { writer ->
                writer.write("package com.rikonardo.pluginscan.preprocessor.generated\n\n")
                writer.write("fun checksList() = listOf<$check>(\n")

                val visitor = ClassVisitor()
                annotated.forEach {
                    it.accept(visitor, writer)
                }

                writer.write(")\n")
            }
        }
        return emptyList()
    }
}

class ClassVisitor : KSTopDownVisitor<OutputStreamWriter, Unit>() {
    override fun defaultHandler(node: KSNode, data: OutputStreamWriter) {
    }

    override fun visitClassDeclaration(
        classDeclaration: KSClassDeclaration,
        data: OutputStreamWriter
    ) {
        super.visitClassDeclaration(classDeclaration, data)
        data.write("    ${classDeclaration.qualifiedName?.asString()}(),\n")
    }
}

class PreprocessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return Preprocessor(environment.codeGenerator, environment.logger)
    }
}