package com.rikonardo.pluginscan.framework.types

class ScanError(val check: Check, val exception: Exception, val step: Step, val classFileName: String? = null) {
    enum class Step {
        BEFORE,
        PROCESS_CLASS,
        AFTER
    }
}
