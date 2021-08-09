import kotlinx.browser.document
import org.w3c.dom.Element
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLSpanElement
import org.w3c.dom.HTMLStyleElement

var progress = 0
var lastPage = ""
val varsOnPage = mutableMapOf<String, String?>()

object Binding {
    val content = document.getElementById("content") as HTMLDivElement
    val loadText = document.getElementById("loadText") as HTMLSpanElement
    val varList = document.getElementById("varList") as HTMLDivElement
    val buttons = document.getElementById("buttons") as HTMLDivElement
    val pageIndicator = document.getElementById("pageIndicator") as HTMLSpanElement
    val contentStyles = document.getElementById("contentStyles") as HTMLStyleElement
}

object Res {
    val vars = mutableMapOf<String, String?>(
        "navbar_disable_tmp" to "1",
        "tw_backup_name" to "1OF demo",
        "of_backup_empty" to "1",
        "of_backup_rw" to "1",
        "tw_operation_state" to "1",
        "tw_operation_status" to "0",
        "of_maintainer" to "2",
        "center_y" to "960",
        "screen_h" to "1920",
        "screen_real_h" to "1920",
        "status_h" to "72",
        "tw_busy" to "0",
        "startup" to "1",
        "status_indent_left" to "16",
        "status_indent_right" to "16",
        "tw_time" to "19:41",
        "tw_storage_free_size" to "10000MB",
        "tw_storage_display_name" to "Internal Storage",
        "tw_clear_destination" to "filemanagerlist"
    )
    var styles = mutableMapOf<String, String?>()
    var images = mutableMapOf<String, String?>()
    var fonts = mutableMapOf<String, String?>()
    var templates = mutableMapOf<String, Element?>()
    var pages = mutableMapOf<String, Element?>()
    var strings = mutableMapOf<String, String?>()
    var keys = mutableMapOf<String, String>()
}