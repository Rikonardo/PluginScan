package style

import kotlinx.browser.document

fun globalStyle() {
    inject("""
        body {
            margin: 0;
            padding: 0;
            font-family: Calibri, sans-serif;
        }
    """.trimIndent())
}

fun inject(styles: String) {
    val style = document.createElement("style")
    style.innerHTML = styles
    document.head!!.appendChild(style)
}
