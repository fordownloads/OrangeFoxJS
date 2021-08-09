package Parsers

import checkCondition
import executeActionString
import forEachChild
import get
import kotlinx.browser.document
import kotlinx.dom.clear
import org.w3c.dom.*
import warnMsg

val div
    get() = document.createElement("div") as HTMLDivElement
val img
    get() = document.createElement("img") as HTMLImageElement

fun HTMLDivElement.loadPage(page: Element?) {
    page.forEachChild {
        when (val tagName = it.tagName) {
            "gesture", "battery" -> Unit
            "action" -> {
                it.getElementsByTagName("touch")[0]?.let { touch ->
                    it.getElementsByTagName("action").asList().forEach { action ->
                        action["function"]?.let { f ->
                            touch["key"]?.let { k ->
                                if (Res.keys[k] == null) Res.keys[k] = ""
                                Res.keys[k] += "$f@${action.textContent};"
                            }
                        }
                    }
                } ?: run {
                    var actionStr = ""
                    it.getElementsByTagName("condition").asList().forEach { cond ->
                        if (!checkCondition(cond)) return@run
                    }
                    it.getElementsByTagName("action").asList().forEach { action ->
                        action["function"]?.let { f ->
                            actionStr += "$f@${action.textContent};"
                        }
                    }
                    executeActionString(actionStr, true)
                }
            }

            "background" -> div.applyProps(it).apply {
                className = "bg"
                style.background = it["color"].parseValue()
            }
            "fill" -> div.applyProps(it).apply {
                style.background = it["color"].parseValue()
            }
            "text", "checkbox" -> div.applyProps(it).apply {
                textContent = it.getElementsByTagName("text")[0]?.textContent.parseValue()
                className = it["style"]?:tagName
            }

            "image" -> img.applyProps(it).className = it["style"]?:tagName

            "keyboard", "console", "progressbar", "partitionlist", "fileselector",
            "listbox", "input", "animation", "slider", "slidervalue" ->
                div.applyProps(it).apply {
                    textContent = tagName
                    className = it["style"]?:tagName
                }

            "template" -> loadPage(Res.templates[it["name"]])
            else -> warnMsg("$tagName: Unknown tag")
        }
    }
}