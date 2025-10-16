package org.mira.utils

import kotlinx.serialization.Serializable
import org.mira.serializer.InstantSerializer
import java.time.Instant


object PaginationToken {

    @Serializable
    data class CreatedAt(
        @Serializable(with = InstantSerializer::class)
        val time: Instant
    )
}