package org.mira.utils

import kotlinx.serialization.json.Json
import java.util.Base64

object PaginationUtils {

    inline fun <reified T> decodeToken(pageToken: String): T {
        val decodedBytes = Base64.getUrlDecoder().decode(pageToken)
        val json = decodedBytes.toString(Charsets.UTF_8)
        return Json.decodeFromString(json)
    }

    inline fun <reified T> encodeToken(token: T): String {
        val json = Json.encodeToString<T>(token)
        val encodedBytes = Base64.getUrlEncoder().encode(json.toByteArray(Charsets.UTF_8))
        return String(encodedBytes, Charsets.UTF_8)
    }
}