package Parsers

import Binding.buttons
import Binding.contentStyles
import Binding.loadText
import Binding.varList
import Res
import add
import forEachChild
import get
import isTag
import loadFont
import org.w3c.dom.Document
import org.w3c.dom.get
import org.w3c.xhr.DOCUMENT
import org.w3c.xhr.XMLHttpRequest
import org.w3c.xhr.XMLHttpRequestResponseType
import progress
import warnMsg

fun loadXML(url: String?) {
    if (url == null) return warnMsg("URL is empty")
    XMLHttpRequest().apply {
        onload = {
            responseXML?.let { parseRoot(it) } ?: run { warnMsg("$url: empty response") }
        }
        onerror = { warnMsg("$it: failed to load") }
        responseType = XMLHttpRequestResponseType.DOCUMENT
        open("GET", "./" + url.replace("/twres/", ""))
        send()
    }
}

fun parseRoot(xml: Document) {
    if (xml.documentElement?.nodeName == "recovery")
        xml.children[0]?.forEachChild { e -> with(e) {
            when (tagName) {
                "details" -> console.info("Theme info: " + e.innerHTML)
                "resources" -> forEachChild {
                    when (it.tagName) {
                        "image" -> Res.images.add(it, it["filename"])
                        "font" -> {
                            loadFont(it["name"], it["filename"])
                            Res.fonts.add(it, "${it["size"]}px '${it["name"]}'")
                        }
                        else -> warnMsg("${it.tagName}: tag not allowed")
                    }
                }
                "include" -> forEachChild {
                    if (it isTag "xml") loadXML(it["default"] ?: it["name"])
                }
                "variables" -> forEachChild {
                    if (it isTag "variable") Res.vars.add(it, it["value"].parseValue())
                }
                "styles" -> forEachChild {
                    if (it isTag "style") it["name"]?.let { v -> parseGlobalProps(it, contentStyles, v) }
                }
                "pages" -> forEachChild {
                    if (it isTag "page") Res.pages.add(it, it)
                }
                "templates" -> forEachChild {
                    if (it isTag "template") Res.templates.add(it, it)
                }
                else -> warnMsg("$tagName: Unknown node")
            }
        }}
    else
        xml.children[0]?.children?.get(1)?.forEachChild {
            if (it isTag "string") Res.strings.add(it, it.textContent)
        }

    if (++progress >= 33) {
        loadText.textContent = "Ready"
        varList.style.width = "300px"
        buttons.style.display = "flex"
    }
}