package com.rikonardo.pluginscan.checks.utils

import com.rikonardo.cafebabe.ClassFile
import com.rikonardo.cafebabe.data.constantpool.*

private fun referenceMethod(
    classFile: ClassFile, classIndex: Int, nameAndTypeIndex: Int, searchedClassName: String, searchedMethodName: String
): Boolean {
    val classRef = classFile.constantPool[classIndex] as ConstantClass
    val className = classFile.constantPool[classRef.nameIndex] as ConstantUtf8
    val methodNameAndType = classFile.constantPool[nameAndTypeIndex] as ConstantNameAndType
    val methodName = classFile.constantPool[methodNameAndType.nameIndex] as ConstantUtf8
    return className.value == searchedClassName && methodName.value == searchedMethodName
}

fun ClassFile.doReferenceMethod(searchedClassName: String, searchedMethodName: String): Boolean {
    return this.constantPool.entries.any {
        if (it is ConstantInterfaceMethodref) {
            if (referenceMethod(this, it.classIndex, it.nameAndTypeIndex, searchedClassName, searchedMethodName))
                return true
        }
        if (it is ConstantMethodref) {
            if (referenceMethod(this, it.classIndex, it.nameAndTypeIndex, searchedClassName, searchedMethodName))
                return true
        }
        false
    }
}

fun ClassFile.doReferenceClass(searchedClassName: String): Boolean {
    return this.constantPool.entries.any {
        if (it is ConstantClass) {
            val className = this.constantPool[it.nameIndex] as ConstantUtf8
            if (className.value == searchedClassName) return@any true
        }
        false
    }
}

fun ClassFile.doReferenceSubclassesOf(searchedClassName: String): Boolean {
    return this.constantPool.entries.any {
        if (it is ConstantClass) {
            val className = this.constantPool[it.nameIndex] as ConstantUtf8
            if (className.value.split("$")[0] == searchedClassName) return@any true
        }
        false
    }
}

fun ClassFile.doReferencedInReturnTypes(searchedClassName: String): Boolean {
    return this.constantPool.entries.any {
        if (it is ConstantNameAndType) {
            val returnType = this.constantPool[it.descriptorIndex] as ConstantUtf8
            val parts = returnType.value.split(")")
            if (parts.size == 2 && parts[1] == "L$searchedClassName;") return@any true
        }
        false
    }

}

fun ClassFile.doReferencedInArgTypes(vararg searchedClassNames: String): Boolean {
    return this.constantPool.entries.any {
        if (it is ConstantNameAndType) {
            val returnType = this.constantPool[it.descriptorIndex] as ConstantUtf8
            val parts = returnType.value.split(")")
            if (parts.size == 2) {
                val args = parseJavaTypes(parts[0].split("(")[1])

                if (
                    searchedClassNames.all { searched ->
                        args.any { type ->
                            (type is JavaReference && type.name == searched) ||
                            (type is JavaArray && type.type is JavaReference && type.type.name == searched)
                        }
                    }
                ) return@any true
            }
        }
        false
    }
}
