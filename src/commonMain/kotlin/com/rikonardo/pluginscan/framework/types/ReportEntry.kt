package com.rikonardo.pluginscan.framework.types

abstract class ReportEntry private constructor() {
    class InClass(val classLocation: String) : ReportEntry()
    class InAny(val location: String) : ReportEntry()
}
