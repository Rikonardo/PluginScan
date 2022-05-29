package layout

import csstype.*
import org.w3c.dom.HTMLInputElement
import org.w3c.files.File
import react.*
import react.css.css
import react.dom.html.AnchorTarget
import react.dom.html.InputType
import react.dom.html.ReactHTML.a
import react.dom.html.ReactHTML.br
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.h1
import react.dom.html.ReactHTML.input
import react.dom.html.ReactHTML.p
import kotlin.js.Date

external interface WelcomePageProps : Props {
    var dragging: Boolean
    var processing: Boolean
    var version: String
    var loadFile: (file: File) -> Unit
}

@Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE", "CAST_NEVER_SUCCEEDS")
val WelcomePage = FC<WelcomePageProps> { props ->
    div {
        css {
            width = 100.pct
            height = 100.pct
            display = Display.flex
            position = Position.relative
            flexDirection = FlexDirection.column
            alignItems = AlignItems.center
            justifyContent = JustifyContent.center
            backgroundColor = Color("#f5f5f5")
        }
        div {
            css {
                maxWidth = 500.px
                width = 100.pct
                textAlign = TextAlign.center
                borderRadius = 3.px
                color = Color("#ffffff")
                backgroundColor = Color("#ff3cac")
                backgroundImage = "linear-gradient(225deg, #FF3CAC 0%, #784BA0 50%, #2B86C5 100%)" as BackgroundImage
                boxShadow = "rgb(0 0 0 / 24%) 0px 3px 8px" as BoxShadow
            }
            if (!props.processing) {
                h1 {
                    +"PluginScan v${props.version}"
                }
                p {
                    css {
                        marginTop = (-10).px
                        fontSize = 18.pt
                        fontStyle = FontStyle.italic
                        cursor = Cursor.pointer
                    }
                    var hovered: Boolean by useState(false)
                    onMouseEnter = { hovered = true }
                    onMouseLeave = { hovered = false }
                    val inputFile: RefObject<HTMLInputElement> = useRef(null)
                    input {
                        css {
                            display = Display.none
                        }
                        onChange = {
                            it.preventDefault()
                            val file = it.target.files?.item(0)
                            if (file != null) {
                                props.loadFile.invoke(file)
                            }
                        }
                        ref = inputFile
                        type = InputType.file
                    }
                    onClick = {
                        inputFile.current?.click()
                    }
                    +if (!props.dragging)
                        if (hovered)
                            "Click to select file"
                        else
                            "Drop a jar file here to scan it"
                    else
                        "Drop file here"
                }
            } else {
                h1 {
                    +"Processing..."
                }
            }
        }
        p {
            css {
                textAlign = TextAlign.center
                marginBottom = 0.px
                color = Color("#404040")
            }
            +"All plugin scanning is done fully clientside, no data being sent to servers."
            br()
            +"We do not collect any uploaded data."
        }
        p {
            css {
                textAlign = TextAlign.center
                marginTop = 10.px
            }
            a {
                css {
                    textDecoration = TextDecoration.none
                    fontWeight = FontWeight.bold
                    color = Color("#4680ff")
                }
                +"Source code"
                href = "https://github.com/Rikonardo/PluginScan"
                target = AnchorTarget._blank
            }
            +" | "
            a {
                css {
                    textDecoration = TextDecoration.none
                    fontWeight = FontWeight.bold
                    color = Color("#4680ff")
                }
                +"PluginScan CLI"
                href = "https://github.com/Rikonardo/PluginScan#pluginscan-cli"
                target = AnchorTarget._blank
            }
        }
        p {
            css {
                textAlign = TextAlign.center
                width = 100.pct
                position = Position.absolute
                left = 0.px
                bottom = 0.px
                fontSize = 12.pt
                color = Color("#404040")
                opacity = Opacity(0.5)
                transition = "opacity 0.25s ease-in-out" as Transition
                hover {
                    opacity = Opacity(1.0)
                }
            }
            +"Copyright © ${Date().getFullYear()} "
            a {
                css {
                    textDecoration = TextDecoration.none
                    fontWeight = FontWeight.bold
                    color = Color("#4680ff")
                }
                +"Rikonardo"
                href = "https://rikonardo.com"
                target = AnchorTarget._blank
            }
            +" and contributors"
        }
    }
}
