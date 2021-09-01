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

fun HTMLDivElement.loadPage(page: Element?, executeActions: Boolean = true) {
    page.forEachChild {
        when (val tagName = it.tagName) {
            "gesture", "battery" -> Unit
            "action" -> {
                it.getElementsByTagName("touch")[0]?.let { touch ->
                    it.getElementsByTagName("action").asList().forEach { action ->
                        action["function"]?.let { f ->
                            when (f) { "ftls", "terminalcommand", "cmd" -> return@let }
                            touch["key"]?.let { k ->
                                if (Res.keys[k] == null) Res.keys[k] = ""
                                Res.keys[k] += "$f@${action.textContent};"
                            }
                        }
                    }
                } ?: run {
                    if (executeActions) {
                        var actionStr = ""
                        it.getElementsByTagName("condition").asList().forEach { cond ->
                            if (!checkCondition(cond)) return@run
                        }
                        it.getElementsByTagName("action").asList().forEach { action ->
                            action["function"]?.let { f ->
                                when (f) { "ftls", "terminalcommand", "cmd" -> return@let }
                                actionStr += "$f@${action.textContent};"
                            }
                        }
                        executeActionString(actionStr, true)
                    }
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
                className = it["style"] ?: tagName
            }
            "button" -> {
                e("btn").applyProps(it).apply {
                    append(e("div").apply { style.content = "var(--image)" })
                    append(e("span").apply {
                        textContent = it.getElementsByTagName("text")[0]?.textContent.parseValue()
                    })
                    className = it["style"] ?: tagName
                }
            }

            "image" -> e("div").applyProps(it).apply {
                style.content = "var(--image)"
                className = it["style"] ?: tagName
            }
            "keyboard" -> e("div").applyProps(it).apply {
                style.content = "var(--image)"
                className = it["style"] ?: tagName
            }

            "input" -> (e("input") as HTMLInputElement).apply {
                className = it["style"] ?: tagName
                value = it.getElementsByTagName("text")[0]?.textContent?.parseValue() ?: ""
            }.applyProps(it)

            "listbox" -> {
                val wrap = div.applyProps(it).apply { className = (it["style"]?:tagName) + " x_listbox" }
                var radioName = ""
                var radioVal = ""

                it.getElementsByTagName("data")[0]?.getAttribute("name")?.let { v ->
                    radioName = v
                    radioVal = Res.vars[v] ?: ""
                }
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
                                when (f) { "ftls", "terminalcommand", "cmd" -> return@let }
                                val data = itemEl.dataset
                                if (data["action"] == undefined) data["action"] = ""
                                data["action"] += "$f@${action.textContent};"
                            }
                        }
                        if (radioName == "")
                            item.getElementsByTagName("data")[0]?.get("variable")?.let { v ->
                                val data = itemEl.dataset
                                val state = Res.vars[v] ?: "0"
                                data["on"] = state
                                if (data["action"] == undefined) data["action"] = ""
                                data["action"] += "set@$v=${if (state == "1") 0 else 1};"
                            }
                        else {
                            val data = itemEl.dataset
                            if (radioVal == item.textContent)
                                data["on"] = "1"
                            else {
                                if (data["action"] == undefined) data["action"] = ""
                                data["action"] += "set@$radioName=${item.textContent};"
                            }
                        }
                        wrap.append(itemEl)
                    }
                }

            }

            "slider" -> {
                div.applyProps(it).apply {
                    className = (it["style"] ?: tagName) + " x_slider_wrap"
                    append(div.apply {
                        append(div.apply { className = "x_slider" })
                        append(div.apply { className = "x_slider_touch" })
                    })
                }
            }

            "fileselector" -> {
                val wrap = div.applyProps(it).apply {
                    style.setProperty("--icon-unselected", "var(--icon-folder)")
                    className = (it["style"]?:tagName) + " x_listbox"
                }


                listOf("..", "Alarms", "Android", "DCIM", "Documents", "Download", "Fox", "Movies", "Music", "Notifications",
                    "Pictures", "Playlists", "Podcasts", "Ringtones", "Sounds").forEach { item ->
                    wrap.append(e("listitem").apply {
                        append(div.apply {append(e("div"))})
                        append(e("span").apply {
                            textContent = item
                        })
                    })
                }
            }

            "partitionlist" -> {
                val wrap = div.applyProps(it).apply { className = (it["style"]?:tagName) + " x_listbox" }

                when(it.getElementsByTagName("listtype")[0]?.get("name")) {
                    "storage" -> listOf("Internal Storage (10050MB)", "MicroSD (140MB)", "USB-OTG (0MB)")
                    "wipe", "part_option" -> listOf("Dalvik Cache", "Cache", "Data", "System", "Vendor", "Internal Storage", "MicroSD", "USB-OTG")

                    "backup", "backup_total", "restore" -> listOf("Boot (128MB)", "Recovery (128MB)",
                        "System (2189MB)", "System Image (4096MB)", "Vendor (489MB)", "Vendor Image (800MB)",
                        "Data (9548MB)", "Storage (28185MB)", "Persist (128MB)", "Firmware (268MB)")
                    "mount" -> listOf("System", "Vendor", "Data", "Cache", "Modem", "MicroSD", "USB-OTG")

                    "flashimg" -> listOf("Boot", "Recovery", "System", "Vendor")
                    else -> listOf("bruh")
                }.forEach { item ->
                    wrap.append(e("listitem").apply {
                        append(div.apply {append(e("div"))})
                        append(e("span").apply {
                            textContent = item
                        })
                    })
                }
            }

            "slidervalue" -> {

            }

            "console", "progressbar", "terminal", "animation" ->
                div.applyProps(it).apply {
                    textContent = tagName
                    className = it["style"]?:tagName
                }

            "template" -> loadPage(Res.templates[it["name"]])
            else -> warnMsg("$tagName: Unknown tag")
        }
    }
}