package lesson09

import lesson09.legacy.check01
import lesson09.legacy.check02
import lesson09.legacy.check03
import lesson09.legacy.check05
import lesson09.legacy.check06
import org.junit.jupiter.api.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class LegacySmokeTest {
    @Test
    fun successStatusHasBothBoundaries() {
        assertFalse(check01(HttpResponse(199, emptyMap(), null)))
        assertTrue(check01(HttpResponse(200, emptyMap(), null)))
        assertTrue(check01(HttpResponse(299, emptyMap(), null)))
        assertFalse(check01(HttpResponse(300, emptyMap(), null)))
    }

    @Test
    fun noContentAllowsAbsentOrEmptyBody() {
        assertTrue(check02(HttpResponse(204, emptyMap(), null)))
        assertTrue(check02(HttpResponse(204, emptyMap(), "")))
        assertFalse(check02(HttpResponse(204, emptyMap(), " ")))
    }

    @Test
    fun headerNameIgnoresCase() {
        val response = HttpResponse(
            200, mapOf("content-type" to listOf("application/json; charset=utf-8")), "{}"
        )
        assertTrue(check03(response))
        assertFalse(check03(response.copy(headers = emptyMap())))
    }

    @Test
    fun twoHeaderConditionsCanUseDifferentValues() {
        val response = HttpResponse(
            200, linkedMapOf("Accept" to listOf("application/json"), "accept" to listOf("text/html")), null
        )
        assertTrue(check05(response))
        assertFalse(check05(response.copy(headers = mapOf("Accept" to listOf("application/json")))))
    }

    @Test
    fun presentNullIsDifferentFromMissingKey() {
        assertTrue(check06(HttpResponse(200, emptyMap(), """{"id":null,"name":"Иванов"}""")))
        assertFalse(check06(HttpResponse(200, emptyMap(), """{"name":"Иванов"}""")))
    }
}
