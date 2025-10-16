package org.mira.utils

import java.util.Base64
import tools.jackson.module.kotlin.jacksonObjectMapper
import tools.jackson.module.kotlin.readValue

object PaginationUtils {

    val mapper = jacksonObjectMapper()

    inline fun <reified T> decodeToken(pageToken: String): T {
        val decodedBytes = Base64.getUrlDecoder().decode(pageToken)
        val json = decodedBytes.toString(Charsets.UTF_8)
        return mapper.readValue(json)
    }

    fun <T> encodeToken(token: T): String {
        val json = mapper.writeValueAsString(token)
        val encodedBytes = Base64.getUrlEncoder().encode(json.toByteArray(Charsets.UTF_8))
        return String(encodedBytes, Charsets.UTF_8)
    }
}