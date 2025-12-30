package org.mira.dynamodb

import kotlinx.serialization.Serializable
import software.amazon.awssdk.services.dynamodb.model.GetItemResponse

@Serializable
data class UserAuthentication(
    val email: String,
    val hash: String,
    val salt: String
) {
    companion object {
        fun from(response: GetItemResponse): UserAuthentication? {
            val item = (if (response.hasItem()) response.item() else null)
            return if (item != null) {
                UserAuthentication(
                    email = item[UserRepository.EMAIL_ATTRIBUTE]?.s() ?: error("Missing email attribute"),
                    hash = item[UserRepository.HASH_ATTRIBUTE]?.s() ?: error("Missing hash attribute"),
                    salt = item[UserRepository.SALT_ATTRIBUTE]?.s() ?: error("Missing salt attribute"),
                )
            } else null
        }
    }
}


@Serializable
data class UserInformation(
    val email: String,
) {
    companion object {
        fun from(response: GetItemResponse): UserInformation? {
            val item = (if (response.hasItem()) response.item() else null)
            return if (item != null) {
                UserInformation(
                    email = item[UserRepository.EMAIL_ATTRIBUTE]?.s() ?: error("Missing email attribute")
                )
            } else null
        }
    }
}
