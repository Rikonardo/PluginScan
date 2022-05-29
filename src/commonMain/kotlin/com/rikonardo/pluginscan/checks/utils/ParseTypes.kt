package com.rikonardo.pluginscan.checks.utils

abstract class JavaType

class JavaVoid : JavaType()
class JavaByte : JavaType()
class JavaChar : JavaType()
class JavaDouble : JavaType()
class JavaFloat : JavaType()
class JavaInt : JavaType()
class JavaLong : JavaType()
class JavaReference(val name: String) : JavaType()
class JavaShort : JavaType()
class JavaBoolean : JavaType()
class JavaArray(val type: JavaType) : JavaType()

fun parseJavaTypes(input: String): List<JavaType> {
    val types = mutableListOf<JavaType>()
    var current = 0
    fun readNext(): JavaType {
        current++
        val type = when (input[current - 1]) {
            'V' -> JavaVoid()
            'B' -> JavaByte()
            'C' -> JavaChar()
            'D' -> JavaDouble()
            'F' -> JavaFloat()
            'I' -> JavaInt()
            'J' -> JavaLong()
            'S' -> JavaShort()
            'Z' -> JavaBoolean()
            '[' -> JavaArray(readNext())
            'L' -> {
                val oldCurr = current
                current = input.indexOf(';', current) + 1
                JavaReference(input.substring(oldCurr, input.indexOf(';', oldCurr)))
            }
            else -> throw IllegalArgumentException("Unknown type: ${input[current - 1]}")
        }
        return type
    }
    while (current < input.length) {
        types.add(readNext())
    }
    return types
}