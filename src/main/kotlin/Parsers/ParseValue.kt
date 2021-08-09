package Parsers

import Res
import varsOnPage

fun String?.parseValue(): String {
    if (this == null) return ""
    var ret = this ?: ""

    if (ret.contains("%"))
        ret = ret.split('%').mapIndexed { p, s ->
            if (p % 2 == 0) s else Res.vars[s].apply { varsOnPage[s] = this }
        }.joinToString("")

    if (ret.contains("{@"))
        ret = ret.replace("{@", "}").split('}').mapIndexed { p, s ->
            if (p % 2 == 0) s else Res.strings[s.split('=')[0]]
        }.joinToString("")

    try {
    when {
        ret.contains('+') -> ret.split('+').let {
            ret = (it[0].toInt() + it[1].toInt()).toString()
        }
        ret.contains('-') -> ret.split('-').let {
            ret = (it[0].toInt() - it[1].toInt()).toString()
        }
    }} catch (e: Exception) {}
    return ret
}