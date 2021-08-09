import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.Element
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLSpanElement
import org.w3c.dom.HTMLStyleElement

var progress = 0
var lastPage = ""
var lastOverlay = ""
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
        "tw_backup_name" to "Demo",
        "of_backup_empty" to "1",
        "of_backup_rw" to "1",
        "tw_operation_state" to "1",
        "tw_operation_status" to "0",
        "of_maintainer" to "2",
        "center_y" to "960",
        "screen_h" to "2160",
        "status_info_y" to "12",
        "screen_original_h" to "2160",
        "screen_real_h" to "2160",
        "tw_edl_mode" to "0",
        "fox_ai_deep_learning_time" to "0",
        "fox_total_backup" to "0",
        "status_h" to "72",
        "tw_busy" to "0",
        "startup" to "1",
        "status_indent_left" to "80",
        "status_indent_right" to "80",
        "tw_reboot_system" to "1",
        "tw_fastboot_mode" to "1",
        "tw_file_location1" to "/sdcard",
        "of_empty_dir" to "0",
        "of_flashlight_enable" to "1",
        "tw_reboot_poweroff" to "1",
        "tw_reboot_recovery" to "1",
        "tw_time" to "19:41",
        "tw_storage_free_size" to "10000",
        "fox_compatibility_fox_device" to window.navigator.userAgent,
        "fox_build_type1" to "Demo",
        "fox_actual_build" to "kotlin",
        "tw_storage_display_name" to "Internal Storage",
        "tw_clear_destination" to "filemanagerlist"
    )
    var images = mutableMapOf<String, String?>()
    var fonts = mutableMapOf<String, String?>()
    var templates = mutableMapOf<String, Element?>()
    var pages = mutableMapOf<String, Element?>()
    var strings = mutableMapOf<String, String?>()
    var keys = mutableMapOf<String, String>()
}