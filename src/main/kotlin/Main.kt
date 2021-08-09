import Binding.buttons
import Binding.content
import Binding.loadText
import Binding.pageIndicator
import Binding.varList
import Parsers.loadXML
import Parsers.parseValue
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.dom.clear
import org.w3c.dom.*
import org.w3c.dom.events.KeyboardEvent


fun main() {
    if (window.location.protocol == "file:") {
        loadText.textContent = "You need a webserver to use this tool"
        return
    }

    buttons.addEventListener("click", { e ->
        val page = (e.target as HTMLElement).dataset["page"]?:""
        when (page) {
            "!prompt" -> window.prompt("Page: ")?.let { setPage(it) }
            "loadvars" -> {
                varList.clear()
                for ((k, v) in varsOnPage.entries) {
                    val item = document.createElement("var") as HTMLElement
                    varList.append(item)
                    item.textContent = "$k: $v"
                    item.onclick = { setVar(k) }
                }
                varList.style.width = "300px"
            }
            else -> setPage(page)
        }
    })

    content.addEventListener("click", { e ->
        val target = e.target as HTMLElement
        if (target.tagName != "INPUT")
            target.dataset["action"]?.let { executeActionString(it) }
    })

    content.addEventListener("keypress", { e ->
        if ((e as KeyboardEvent).key != "Enter") return@addEventListener
        val target = e.target as HTMLElement
        if (target.tagName == "INPUT")
            target.dataset["action"]?.let { executeActionString(it) }
    })

    loadXML("languages/en.xml")
    loadXML("ui.xml")
}