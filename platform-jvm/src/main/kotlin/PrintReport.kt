import com.github.ajalt.mordant.rendering.TextColors.*
import com.github.ajalt.mordant.rendering.TextColors.Companion.rgb
import com.github.ajalt.mordant.rendering.TextStyle
import com.rikonardo.pluginscan.framework.types.CheckReport
import com.rikonardo.pluginscan.framework.types.ReportEntry
import com.rikonardo.pluginscan.framework.types.RiskLevel

fun getColors(risk: RiskLevel): Pair<TextStyle, TextStyle> {
    return when (risk) {
        RiskLevel.LOW -> {
            rgb("#aeae80") to rgb("#d7d7c1")
        }
        RiskLevel.MODERATE -> {
            rgb("#be9117") to rgb("#ebc660")
        }
        RiskLevel.HIGH -> {
            rgb("#bc3f3c") to rgb("#d7817f")
        }
        RiskLevel.CRITICAL -> {
            rgb("#9e2927") to rgb("#dd7a78")
        }
    }
}

fun printReport(report: CheckReport) {
    val colors = getColors(report.risk)
    println("${(colors.first.bg + black)(" ${report.risk.name} ")} ${colors.first(report.message)}")
    val description = report.description?.split("\n") ?: listOf()
    description.forEachIndexed { index, s ->
        if (index == description.lastIndex && report.entries.isEmpty()) {
            println(colors.second(" ┗ $s"))
        } else {
            println(colors.second(" ┃ $s"))
        }
    }
    report.entries.forEachIndexed { index, entry ->
        val char = if (index == report.entries.size - 1) "┗" else "┣"
        when (entry) {
            is ReportEntry.In -> {
                println(" ${colors.second(char)} ${brightMagenta("in")} ${brightWhite(entry.classLocation)}")
            }
        }
    }
    println()
}