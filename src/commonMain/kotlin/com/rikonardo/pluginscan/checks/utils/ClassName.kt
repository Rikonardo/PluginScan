package com.rikonardo.pluginscan.checks.utils

fun className(fileName: String): String {
    return fileName.substringBeforeLast(".").replace("/", ".")
}