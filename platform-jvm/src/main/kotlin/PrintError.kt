import com.github.ajalt.mordant.rendering.TextColors.*
import com.rikonardo.pluginscan.framework.types.ScanError

fun printError(error: ScanError) {
    println(red(
        "${
            (gray.bg + red)(" SCAN ERROR ")
        } in ${
            brightWhite(error.check::class.simpleName!!)
        } check at ${
            brightWhite(error.step.name.lowercase())
        } step" +
        if (error.classFileName != null) " during scan of \"${brightWhite(error.classFileName!!)}\"" else ""
    ))
    println(red(" ┣ ${error.exception::class.qualifiedName}: ${error.exception.message}"))
    error.exception.stackTrace.forEachIndexed { index, st ->
        val char = if (index == error.exception.stackTrace.lastIndex) '┗' else '┃'
        println(red(" $char ${magenta("at")} $st"))
    }
    println()
}
