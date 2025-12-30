package org.mira.dynamodb

import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest
import software.amazon.awssdk.services.dynamodb.model.GetItemResponse
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest

class UserRepository(private val dynamoDbClient: DynamoDbClient) {

    companion object {
        const val USERS_TABLE_NAME = "MemoryBoxUsers"
        const val EMAIL_ATTRIBUTE = "email"
        const val HASH_ATTRIBUTE = "hash"
        const val SALT_ATTRIBUTE = "salt"
    }

    fun registerUser(email: String, hash: String, salt: String) {

        val item = mapOf(
            EMAIL_ATTRIBUTE to AttributeValue.fromS(email),
            HASH_ATTRIBUTE to AttributeValue.fromS(hash),
            SALT_ATTRIBUTE to AttributeValue.fromS(salt),
        )

        val request = PutItemRequest.builder()
            .tableName(USERS_TABLE_NAME)
            .item(item)
            .build()

        dynamoDbClient.putItem(request)
    }

    fun getUser(email: String): UserInformation? {

        val request = GetItemRequest.builder()
            .tableName(USERS_TABLE_NAME)
            .key(
                mapOf(
                    EMAIL_ATTRIBUTE to AttributeValue.builder().s(email).build()
                )
            )
            .build()

        val response: GetItemResponse = dynamoDbClient.getItem(request)

        return UserInformation.from(response)
    }

    fun getUserAuthentication(email: String): UserAuthentication? {
        val request = GetItemRequest.builder()
            .tableName(USERS_TABLE_NAME)
            .key(
                mapOf(
                    EMAIL_ATTRIBUTE to AttributeValue.builder().s(email).build()
                )
            )
            .build()

        val response: GetItemResponse = dynamoDbClient.getItem(request)

        return UserAuthentication.from(response)
    }
}
