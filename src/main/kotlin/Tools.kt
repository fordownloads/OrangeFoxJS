import Binding.content
import Binding.loadText
import Binding.pageIndicator
import Parsers.loadPage
import Parsers.parseValue
import kotlinx.browser.window
import org.w3c.dom.Element
import org.w3c.dom.asList
import kotlin.js.Date


operator fun Element.get(name: String): String? { return getAttribute(name) }

fun Element?.forEachChild(action: (Element) -> Unit) = this?.children?.asList()?.forEach(action)?: run {
    warnMsg("Element has no children"); null
}

infix fun Element?.isTag(tag: String): Boolean {
    if (this?.tagName?.equals(tag) == true)
        return true
    warnMsg("${this?.tagName}: tag not allowed")
    return false
}

fun <T> MutableMap<String, T>.add(el: Element, value: T) =
    el["name"]?.let { put(it, value) }

fun checkCondition(cond: Element): Boolean {
    val var1 = "%${cond["var1"]}%".parseValue()
    if (var1 == "fileexists") return true
    val var2 = (cond["var2"] ?: "1").parseValue()
    cond["var1"]?.let { varsOnPage[it] = var1 }
    return when (cond["op"] ?: "=") {
        "="  -> var1 == var2
        "!=" -> var1 != var2
        ">=" -> var1 >= var2
        "<=" -> var1 <= var2
        ">"  -> var1 >  var2
        "<"  -> var1 <  var2
        "modified" -> false
        else -> true
    }
}

fun setPage(page: String = lastPage, executeActions: Boolean = true) {
    lastPage = page
    console.info("Changed page: $page")
    pageIndicator.textContent = page
    content.innerHTML = ""
    Res.keys.clear()
    content.loadPage(Res.pages[page.parseValue()], executeActions)
    if (lastOverlay != "")
    content.loadPage(Res.pages[lastOverlay.parseValue()], executeActions)
}

fun setVar(name: String) {
    Res.vars[name] = window.prompt("Value for $name:", Res.vars[name]?:"")
    setPage()
}

var maxAct = 0
var timer = 0

fun executeActionString(actions: String, pageAction: Boolean = false) {
    if (++maxAct >= 30) {
        window.alert("Too many actions detected")
        return
    }

    window.clearTimeout(timer)
    timer = window.setTimeout({maxAct = 0}, 1500)

    var skipSetPage = false
    var executeActions = false
    console.info(actions)
    actions.split(';').forEach {
        if (it.isEmpty()) return@forEach
        val (action, param) = it.split('@')
        when (action) {
            "set" -> {
                val (k, v) = param.split('=')
                Res.vars[k] = v.parseValue()
            }
            "appenddatetobackupname" -> Res.vars["tw_backup_name"] += Date().toISOString()
            "generatebackupname" -> Res.vars["tw_backup_name"] = "Demo-" + Date().toISOString()
            "page" -> {
                executeActions = true
                if (pageAction) setPage(param) else lastPage = param
            }
            "overlay" -> lastOverlay = param
            "checkbackupfolder" -> {
                executeActions = true
                if (pageAction) setPage("restore_prep") else lastPage = "restore_prep"
            }
            "key" -> {
                skipSetPage = true
                Res.keys[param]?.let { v -> executeActionString(v) }
            }
        }
    }
    if (!skipSetPage && !pageAction) setPage(lastPage, executeActions)
}

fun loadFont(name: String?, url: String?) =
    js("""
        new FontFace(name, 'url(./fonts/'+url+')').load().then(function (face) {
            document.fonts.add(face)
        }).catch(function (err) { warnMsg(err.toString()) });
    """)

fun warnMsg(text: String) {
    console.warn(text)
    //loadText.textContent = text
}