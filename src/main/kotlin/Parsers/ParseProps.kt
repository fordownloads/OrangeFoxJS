package Parsers

import Binding.content
import checkCondition
import forEachChild
import get
import kotlinx.browser.document
import org.w3c.dom.*
import warnMsg

fun parseGlobalProps(node: Element, styleSheet: HTMLStyleElement, name: String) {
    (document.createElement("div") as HTMLDivElement).let {
        parseProps(node, it)
        styleSheet.innerText += ".$name {${it.style.cssText}}"
    }
}

fun HTMLElement.applyProps(node: Element): HTMLElement {
    if (parseProps(node, this)) content.append(this)
    return this
}

fun parseProps(node: Element, element: HTMLElement): Boolean {
    val style = element.style
    var visible = true
    node.forEachChild {
        when (it.tagName) {
            "condition" -> if (!checkCondition(it)) {
                visible = false
                return@forEachChild
            }
            "placement" -> {
                style.apply {
                    it["x"]?.let { v -> left = v.parseValue() + "px" }
                    it["y"]?.let { v -> top = v.parseValue() + "px" }
                    it["w"]?.let { v -> width = v.parseValue() + "px" }
                    it["h"]?.let { v -> height = v.parseValue() + "px" }

                    if (node.tagName != "fill")
                        when (it["placement"]) {
                            "1" -> transform = "translateX(-100%)"
                            "2" -> transform = "translateY(-100%)"
                            "3" -> transform = "translate(-100%, -100%)"
                            "4" -> transform = "translate(-50%, -50%)"
                            "5" -> transform = "translateX(-50%)"
                        }
                }
            }
            "font" -> {
                it["resource"]?.let { v -> style.font = Res.fonts[v] ?: "" }
                it["color"]?.let { v -> style.color = v.parseValue() }
                it["highlightcolor"]?.let { v ->
                    style.setProperty("--hover-color", v.parseValue())
                }
            }
            "color" -> {
                it["foreground"]?.let { v -> style.color = v.parseValue() }
                it["background"]?.let { v -> style.backgroundColor = v.parseValue() }
            }
            "action" -> it["function"]?.let { f ->
                when (f) { "ftls", "terminalcommand", "cmd" -> return@let }
                val data = element.dataset
                if (data["action"] == undefined) data["action"] = ""
                data["action"] += "$f@${it.textContent};"
            }
            "fill", "background" -> it["color"]?.let { v -> style.backgroundColor = v.parseValue() }
            "highlight" -> it["color"]?.let { v ->
                style.setProperty("--hover-background", v.parseValue())
            }
            "fastscroll" -> {
                it["rectcolor"]?.let { v -> style.setProperty("--scroll-color",v.parseValue()) }
                it["w"]?.let { v -> style.setProperty("--scroll-w",v.parseValue()+"px") }
            }
            "image" -> it["resource"]?.let { v -> style.setProperty("--image","url('./images/${Res.images[v]}.png')") }
            "iconsize" -> {
                it["w"]?.let { v -> style.setProperty("--icon-w",v.parseValue()+"px") }
                it["h"]?.let { v -> style.setProperty("--icon-h",v.parseValue()+"px") }
                it["padding"]?.let { v -> style.setProperty("--icon-padding",v.parseValue()+"px") }
            }
            "icon" -> {
                it["selected"]?.let { v -> style.setProperty("--icon-selected", "url('./images/${Res.images[v]}.png')") }
                it["unselected"]?.let { v -> style.setProperty("--icon-unselected", "url('./images/${Res.images[v]}.png')") }
                it["folder"]?.let { v -> style.setProperty("--icon-folder", "url('./images/${Res.images[v]}.png')") }
                it["file"]?.let { v -> style.setProperty("--icon-file", "url('./images/${Res.images[v]}.png')") }
            }
            "resource" -> {
                it["base"]?.let { v -> style.setProperty("--slider-base", "url('./images/${Res.images[v]}.png')") }
                it["used"]?.let { v -> style.setProperty("--slider-used", "url('./images/${Res.images[v]}.png')") }
                it["touch"]?.let { v -> style.setProperty("--slider-touch", "url('./images/${Res.images[v]}.png')") }
            }
            "layout" -> {
                it["resource"]?.let { v -> style.setProperty("--image", "url('./images/${Res.images[v]}.png')") }
            }
            //"image" -> it["resource"]?.let { v -> style.content = "url('./images/${Res.images[v]}.png')" }
        }
    }
    return visible
}