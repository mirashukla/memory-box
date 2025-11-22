package org.mira.dynamodb

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val email: String,
    val password: String
)

@Serializable
data class UserInformation(
    val email: String,
)