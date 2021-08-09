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
fun e(type: String) = document.createElement(type) as HTMLElement

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
            "button" -> {
                e("btn").applyProps(it).apply {
                    append(e("div").apply { style.content = "var(--image)" })
                    append(e("span").apply { textContent = it.getElementsByTagName("text")[0]?.textContent.parseValue() })
                    className = it["style"]?:tagName
                }
            }

            "image" -> e("div").applyProps(it).apply {
                style.content = "var(--image)"
                className = it["style"] ?: tagName
            }

            "input" -> e("input").applyProps(it).className = it["style"] ?: tagName

            "listbox" -> {
                val wrap = div.applyProps(it).apply { className = (it["style"]?:tagName) + " x_listbox" }
                it.getElementsByTagName("listitem").asList().forEach { item ->
                    var visible = true
                    item.getElementsByTagName("condition").asList().forEach { cond ->
                        if (!checkCondition(cond)) {
                            visible = false
                            return@forEach
                        }
                    }
                    if (visible) {
                        val itemEl = e("listitem").apply {
                            item.getElementsByTagName("icon")[0]?.let { icon -> icon["res"]?.let { v ->
                                style.setProperty("--icon-unselected", "url('./images/${Res.images[v]}.png')")
                            }}

                            append(div.apply {append(e("div"))})
                            append(e("span").apply {
                                textContent = item["name"].parseValue()
                            })
                        }
                        item.getElementsByTagName("action").asList().forEach { action ->
                            action["function"]?.let { f ->
                                console.log("Action","$f@${action.textContent};")
                                val data = itemEl.dataset
                                if (data["action"] == undefined) data["action"] = ""
                                data["action"] += "$f@${action.textContent};"
                            }
                        }
                        wrap.append(itemEl)
                    }
                }

            }

            "keyboard", "console", "progressbar", "partitionlist", "fileselector",
             "animation", "slider", "slidervalue" ->
                div.applyProps(it).apply {
                    textContent = tagName
                    className = it["style"]?:tagName
                }

            "template" -> loadPage(Res.templates[it["name"]])
            else -> warnMsg("$tagName: Unknown tag")
        }
    }
}