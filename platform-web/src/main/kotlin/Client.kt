import com.rikonardo.pluginscan.framework.PluginScan
import external.JSZip
import kotlinx.browser.document
import layout.AppRoot
import react.create
import react.dom.render
import style.globalStyle

fun main() {
    val container = document.createElement("div")
    document.body!!.appendChild(container)

    globalStyle()
    val welcome = AppRoot.create {
        version = PluginScan.VERSION
    }
    render(welcome, container)
}
