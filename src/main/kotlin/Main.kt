import Binding.buttons
import Binding.content
import Binding.loadText
import Binding.pageIndicator
import Parsers.loadXML
import Parsers.parseValue
import kotlinx.browser.window
import org.w3c.dom.*



fun main() {
    if (window.location.protocol == "file:") {
        loadText.textContent = "You need a webserver to use this tool"
        return
    }

    buttons.addEventListener("click", { e ->
        val page = (e.target as HTMLElement).dataset["page"]?:""
        if (page == "!prompt")
            window.prompt("Page: ")?.let { setPage(it) }
        else
            setPage(page)
    })

    content.addEventListener("click", { e ->
        (e.target as HTMLElement).dataset["action"]?.let { executeActionString(it) }
    })

    loadXML("languages/en.xml")
    loadXML("ui.xml")
}