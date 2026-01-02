package org.mira.authentication

sealed class Authentication {
    object Post : Authentication()
    object Get : Authentication()
    object Unknown : Authentication()

    companion object {
        fun fromMethod(method: String?) =
            when (method) {
                "GET" -> Get
                "POST" -> Post
                else -> Unknown
            }
    }
}