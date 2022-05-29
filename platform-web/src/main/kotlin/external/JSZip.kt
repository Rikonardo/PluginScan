package external

import kotlin.js.Date
import kotlin.js.Promise

@JsModule("jszip")
@JsNonModule
external class JSZip {
    companion object {
        fun loadAsync(data: dynamic): Promise<JSZip>
    }

    var files: dynamic

    fun forEach(callback: (relativePath: String, file: JSZipObject) -> Unit)
}

@JsModule("jszip")
@JsNonModule
external interface JSZipObject {
    val name: String
    val dir: Boolean
    val date: Date
    val comment: String
    val unixPermissions: String
    val dosPermissions: String
    val options: dynamic

    fun async(type: String): Promise<Any>
}
