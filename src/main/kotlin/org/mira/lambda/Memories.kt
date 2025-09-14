package org.mira.lambda

sealed class Memories {
    object Post : Memories()
    object Get : Memories()
    object Unknown : Memories()

    companion object {
        fun fromMethod(method: String) =
            when (method) {
            "POST" -> Post
            "GET" -> Get
            else -> Unknown
        }
    }
}