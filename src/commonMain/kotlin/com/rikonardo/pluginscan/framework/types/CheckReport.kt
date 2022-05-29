package com.rikonardo.pluginscan.framework.types

class CheckReport(
    val risk: RiskLevel,
    val message: String,
    val description: String?,
    val entries: List<ReportEntry>,
    val providedBy: Check
)
