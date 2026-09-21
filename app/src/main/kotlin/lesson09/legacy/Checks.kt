package lesson09.legacy

import lesson09.HttpResponse

// Исходные проверки: сохраняйте этот файл без изменений.
fun check01(response: HttpResponse): Boolean {
    if (response.status < 200 || response.status > 299) return false
    return true
}

fun check02(response: HttpResponse): Boolean {
    if (response.status != 204) return false
    if (response.body != null && response.body != "") return false
    return true
}

fun check03(response: HttpResponse): Boolean {
    if (response.status != 200) return false
    val values = headerValues(response, "Content-Type")
    if (values.none { it.startsWith("application/json") }) return false
    if (response.body == null || response.body.isEmpty()) return false
    return true
}

fun check04(response: HttpResponse): Boolean {
    if (response.status != 201) return false
    val values = headerValues(response, "Location")
    if (values.none { it.startsWith("/users/") }) return false
    return true
}

fun check05(response: HttpResponse): Boolean {
    if (response.status != 200) return false
    val values = headerValues(response, "Accept")
    if (values.none { it == "application/json" }) return false
    if (values.none { it == "text/html" }) return false
    return true
}

fun check06(response: HttpResponse): Boolean {
    if (response.status != 200) return false
    val fields = flatObject(response.body) ?: return false
    if (!fields.containsKey("id")) return false
    if (fields["name"] != "Иванов") return false
    return true
}

fun check07(response: HttpResponse): Boolean {
    if (response.status != 200) return false
    val fields = flatObject(response.body) ?: return false
    if (fields["id"] != 42L) return false
    if (fields["active"] != true) return false
    return true
}

fun check08(response: HttpResponse): Boolean {
    if (response.status != 200) return false
    val fields = flatObject(response.body) ?: return false
    if (!fields.containsKey("note")) return false
    if (fields["note"] != null) return false
    if (fields["deleted"] != false) return false
    return true
}

fun check09(response: HttpResponse): Boolean {
    if (response.status != 422) return false
    val values = headerValues(response, "Content-Type")
    if (values.none { it.startsWith("application/json") }) return false
    val fields = flatObject(response.body) ?: return false
    if (fields["error"] != "invalid") return false
    if (fields["retry"] != false) return false
    return true
}

fun check10(response: HttpResponse): Boolean {
    if (response.status != 200) return false
    val values = headerValues(response, "X-Trace")
    if (values.none { it.startsWith("trace-") }) return false
    val fields = flatObject(response.body) ?: return false
    if (fields["quote"] != "он сказал \"да\"") return false
    if (fields["path"] != "C:\\tmp") return false
    return true
}

private fun headerValues(response: HttpResponse, name: String): List<String> =
    response.headers.entries
        .filter { it.key.equals(name, ignoreCase = true) }
        .flatMap { it.value }

// При повторении ключа сохраняется последнее значение.
private fun flatObject(source: String?): Map<String, Any?>? {
    if (source == null) return null
    val token = Regex(
        """[ \t\r\n]+|"(?:[^"\\\x00-\x1F]|\\["\\])*+"|-?(?:0|[1-9][0-9]*)|true|false|null|[{}:,]"""
    )
    val tokens = mutableListOf<String>()
    var offset = 0
    while (offset < source.length) {
        val match = token.matchAt(source, offset) ?: return null
        val text = match.value
        if (text[0] !in " \t\r\n") tokens.add(text)
        offset = match.range.last + 1
    }
    var index = 0
    fun take(expected: String): Boolean {
        if (tokens.getOrNull(index) != expected) return false
        index += 1
        return true
    }
    fun decoded(value: String): String {
        val result = StringBuilder()
        var position = 1
        while (position < value.lastIndex) {
            if (value[position] == '\\') position += 1
            result.append(value[position++])
        }
        return result.toString()
    }
    if (!take("{")) return null
    val fields = linkedMapOf<String, Any?>()
    if (take("}")) return if (index == tokens.size) fields else null
    while (true) {
        val key = tokens.getOrNull(index++) ?: return null
        if (!key.startsWith('"')) return null
        if (!take(":")) return null
        val value = tokens.getOrNull(index++) ?: return null
        fields[decoded(key)] = when {
            value.startsWith('"') -> decoded(value)
            value == "true" -> true
            value == "false" -> false
            value == "null" -> null
            else -> value.toLongOrNull() ?: return null
        }
        if (take("}")) return if (index == tokens.size) fields else null
        if (!take(",")) return null
    }
}
