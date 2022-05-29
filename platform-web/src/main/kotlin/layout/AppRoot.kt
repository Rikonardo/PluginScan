package layout

import com.rikonardo.pluginscan.framework.PluginScan
import com.rikonardo.pluginscan.framework.jar.JarEntry
import com.rikonardo.pluginscan.framework.jar.JarFile
import com.rikonardo.pluginscan.framework.types.ScanResult
import csstype.vh
import csstype.vw
import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.files.FileReader
import react.dom.html.ReactHTML.div
import external.JSZip
import external.JSZipObject
import kotlinext.js.Object
import org.khronos.webgl.Uint8Array
import org.w3c.files.File
import react.*
import react.css.css
import kotlin.js.Date
import kotlin.js.Promise

external interface AppRootProps : Props {
    var version: String
}

@Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE", "CAST_NEVER_SUCCEEDS")
val AppRoot = FC<AppRootProps> { props ->
    val scanResult: ScanResult by useState(ScanResult(listOf(), listOf()))
    var scanned : Boolean by useState(false)
    var dragging : Boolean by useState(false)
    var processing : Boolean by useState(false)
    var fileName : String by useState("")
    var scanTime : Int by useState(0)
    fun loadFiles(files: List<Pair<String, ByteArray>>) {
        val startTime = Date.now().toLong()
        Object.assign(scanResult, PluginScan.scan(JarFile(files.map {
            JarEntry(it.first, it.second)
        }), groupOutput = true))
        scanned = true
        scanTime = (Date.now() - startTime).toInt()
        processing = false
    }
    fun parseJar(data: dynamic) {
        val files = mutableListOf<Pair<String, ByteArray>>()
        JSZip.loadAsync(data).then { jsZip ->
            val keys = Object.keys(jsZip.files).toList().filter { !jsZip.files[it].dir as Boolean }
            Promise.all(keys.map {
                val file = jsZip.files[it] as JSZipObject
                return@map file.async("uint8array")
            }.toTypedArray()).then {
                for (i in it.indices) {
                    val uint8Array: Uint8Array = it[i] as Uint8Array
                    val byteArray = uint8Array.unsafeCast<ByteArray>()
                    files.add(keys[i] to byteArray)
                }
                loadFiles(files)
            }
        }.catch {
            console.error(it)
            processing = false
        }
    }
    fun loadJarFile(file: File) {
        processing = true
        dragging = false
        scanned = false
        fileName = file.name
        val reader = FileReader()
        reader.onload = {
            parseJar(reader.result)
        }
        reader.readAsArrayBuffer(file)
    }
    useEffect(listOf(scanned, fileName)) {
        document.title = if (scanned)
            "$fileName | PluginScan - Minecraft plugin inspector"
        else
            "PluginScan - Minecraft plugin inspector"
    }
    useEffectOnce {
        var dragTimer : Int? = null
        document.addEventListener("dragenter", { it.preventDefault() })
        document.addEventListener("dragover", {
            it.preventDefault()
            if (dragTimer != null) window.clearTimeout(dragTimer!!)
            dragging = true
        })
        document.addEventListener("dragleave", {
            it.preventDefault()
            if (dragTimer != null) window.clearTimeout(dragTimer!!)
            dragTimer = window.setTimeout( {
                dragging = false
            }, 85)
        })
        document.addEventListener("drop", {
            it.preventDefault()
            val event: dynamic = it
            if (event.dataTransfer.files.length == 0) {
                dragging = false
                return@addEventListener
            }
            loadJarFile(event.dataTransfer.files[0] as File)
        })
    }
    div {
        css {
            width = 100.vw
            height = 100.vh
        }
        if (!scanned || dragging) {
            WelcomePage {
                this@WelcomePage.dragging = dragging
                this@WelcomePage.processing = processing
                this@WelcomePage.version = props.version
                this@WelcomePage.loadFile = { file -> loadJarFile(file) }
            }
        } else {
            ResultsPage {
                this@ResultsPage.version = props.version
                this@ResultsPage.scanTime = scanTime
                this@ResultsPage.fileName = fileName
                this@ResultsPage.scanResult = scanResult
                this@ResultsPage.homeScreen = {
                    scanned = false
                }
            }
        }
    }
}
