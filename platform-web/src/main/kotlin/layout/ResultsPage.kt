package layout

import com.rikonardo.pluginscan.framework.types.ScanResult
import com.rikonardo.pluginscan.framework.types.ReportEntry
import com.rikonardo.pluginscan.framework.types.RiskLevel
import csstype.*
import react.FC
import react.Props
import react.css.PropertiesBuilder
import react.css.css
import react.dom.html.ReactHTML.b
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.h1
import react.dom.html.ReactHTML.p
import react.dom.html.ReactHTML.span

external interface ResultsPageProps : Props {
    var scanTime: Int
    var fileName: String
    var scanResult: ScanResult
    var version: String
    var homeScreen: () -> Unit
}

private fun PropertiesBuilder.reportCardRisk(risk: RiskLevel) {
    when (risk) {
        RiskLevel.LOW -> {
            borderColor = Color("#9bff00")
            backgroundColor = Color("#f3ffe2")
        }
        RiskLevel.MODERATE -> {
            borderColor = Color("#ffd600")
            backgroundColor = Color("#fffae2")
        }
        RiskLevel.HIGH -> {
            borderColor = Color("#ff5400")
            backgroundColor = Color("#fff2e2")
        }
        RiskLevel.CRITICAL -> {
            borderColor = Color("#ff0000")
            backgroundColor = Color("#ffe2e2")
        }
    }
}

private fun PropertiesBuilder.badgeRisk(risk: RiskLevel) {
    backgroundColor = when (risk) {
        RiskLevel.LOW -> Color("#9bff00")
        RiskLevel.MODERATE -> Color("#ffd600")
        RiskLevel.HIGH -> Color("#ff5400")
        RiskLevel.CRITICAL -> Color("#ff0000")
    }
}

@Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE", "CAST_NEVER_SUCCEEDS")
val ResultsPage = FC<ResultsPageProps> { props ->
    div {
        css {
            display = Display.flex
            flexDirection = FlexDirection.column
            height = 100.vh
        }
        div {
            css {
                width = "calc(100% - 40px)" as Width
                backgroundColor = Color("#85FFBD")
                color = Color("#404040")
                backgroundImage = "linear-gradient(45deg, #85FFBD 0%, #FFFB7D 100%)" as BackgroundImage
                boxShadow = "rgb(0 0 0 / 15%) 0px 3px 8px" as BoxShadow
                padding = Padding(15.px, 20.px)
                position = Position.relative
                zIndex = ZIndex(0)
            }
            h1 {
                css { margin = 0.px }
                +"PluginScan v${props.version}"
            }
            p {
                css {
                    margin = 0.px
                    marginBottom = 5.px
                    marginTop = 6.px
                }
                +"Scan time: ${props.scanTime}ms"
            }
            p {
                css {
                    margin = 0.px
                    marginBottom = 5.px
                }
                +"File name: "
                b {
                    +props.fileName
                }
            }
            span {
                css {
                    position = Position.absolute
                    top = 15.px
                    right = 20.px
                    fontSize = 12.pt
                    fontWeight = FontWeight.bold
                    color = Color("#4680ff")
                    cursor = Cursor.pointer
                }
                onClick = {
                    props.homeScreen.invoke()
                }
                +"Back"
            }
        }
        div {
            css {
                height = 100.pct
                overflowY = OverflowY.auto
                width = "calc(100% - 30px)" as Width
                padding = Padding(15.px, 15.px)
            }
            if (props.scanResult.reports.isEmpty()) {
                p {
                    css {
                        margin = 0.px
                    }
                    +"Nothing found"
                }
            }
            for (report in props.scanResult.reports) {
                div {
                    css {
                        border = "2px solid" as Border
                        borderRadius = 3.px
                        marginBottom = 20.px
                        reportCardRisk(report.risk)
                    }
                    div {
                        css {
                            display = Display.flex
                        }
                        span {
                            css {
                                padding = Padding(4.px, 14.px, 6.px, 12.px)
                                color = Color("#404040")
                                fontWeight = FontWeight.bold
                                borderRadius = "0 0 3px 0" as BorderRadius
                                fontSize = 12.pt
                                height = "fit-content" as Height
                                badgeRisk(report.risk)
                            }
                            +report.risk.name
                        }
                        span {
                            css {
                                fontSize = 14.pt
                                padding = Padding(3.px, 10.px)
                            }
                            +report.message
                        }
                    }
                    div {
                        css {
                            padding = Padding(10.px, 12.px)
                        }
                        if (report.description != null) {
                            p {
                                css {
                                    margin = 0.px
                                    paddingBottom = 5.px
                                }
                                +report.description!!
                            }
                        }
                        for (entry in report.entries) {
                            when (entry) {
                                is ReportEntry.InClass, is ReportEntry.InAny -> {
                                    p {
                                        css {
                                            margin = 0.px
                                            fontFamily = FontFamily.monospace
                                        }
                                        span {
                                            css {
                                                marginRight = 5.px
                                                color = Color("#ff00ff")
                                            }
                                            +"in"
                                        }
                                        span {
                                            css {
                                                marginRight = 5.px
                                                color = Color("#404040")
                                            }
                                            +if (entry is ReportEntry.InClass)
                                                entry.classLocation
                                            else
                                                (entry as ReportEntry.InAny).location
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (props.scanResult.errors.isNotEmpty()) {
                p {
                    css {
                        margin = 0.px
                        marginBottom = 20.px
                        color = Color("#ff0000")
                    }
                    +"${props.scanResult.errors.size} error(s) have occurred during the scan:"
                }
            }
            for (error in props.scanResult.errors) {
                div {
                    css {
                        border = "2px solid" as Border
                        borderRadius = 3.px
                        marginBottom = 20.px
                        borderColor = Color("#ff0000")
                        backgroundColor = Color("#ffe2e2")
                    }
                    div {
                        css {
                            display = Display.flex
                        }
                        span {
                            css {
                                padding = Padding(4.px, 14.px, 6.px, 12.px)
                                color = Color("#ff0000")
                                fontWeight = FontWeight.bold
                                borderRadius = "0 0 3px 0" as BorderRadius
                                fontSize = 12.pt
                                height = "fit-content" as Height
                                backgroundColor = Color("#b0b0b0")
                            }
                            +"SCAN ERROR"
                        }
                        span {
                            css {
                                fontSize = 14.pt
                                padding = Padding(3.px, 10.px)
                            }
                            +"Error in ${error.check::class.simpleName} check at ${error.step.name} step ${
                                if (error.classFileName != null)
                                    " during scan of \"${error.classFileName!!}\"" else ""
                            }"
                        }
                    }
                    div {
                        css {
                            padding = Padding(10.px, 12.px)
                        }
                        p {
                            css {
                                margin = 0.px
                                fontFamily = FontFamily.monospace
                                whiteSpace = WhiteSpace.pre
                            }
                            +error.exception.stackTraceToString()
                        }
                    }
                }
            }
        }
    }
}
