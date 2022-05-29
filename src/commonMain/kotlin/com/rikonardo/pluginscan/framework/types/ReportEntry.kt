package com.rikonardo.pluginscan.framework.types

abstract class ReportEntry private constructor() {
    class In(val classLocation: String) : ReportEntry()
}
