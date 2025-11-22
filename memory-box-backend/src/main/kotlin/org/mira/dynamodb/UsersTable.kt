package org.mira.dynamodb

import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest

class UsersTable(private val dynamoDbClient: DynamoDbClient) {

    private companion object {
        const val USERS_TABLE_NAME = "MemoryBoxUsers"
        const val EMAIL_ATTRIBUTE = "email"
        const val PASSWORD_ATTRIBUTE = "password"
    }

    fun registerUser(email: String, password: String) {
        val item = mapOf(
            EMAIL_ATTRIBUTE to AttributeValue.fromS(email),
            PASSWORD_ATTRIBUTE to AttributeValue.fromS(password),
        )

        val request = PutItemRequest.builder()
            .tableName(USERS_TABLE_NAME)
            .item(item)
            .build()

        dynamoDbClient.putItem(request)
    }

    fun getUser(email: String): UserInformation {

        val request = GetItemRequest.builder()
            .tableName(USERS_TABLE_NAME)
            .key(
                mapOf(
                    "email" to AttributeValue.builder().s(email).build()
                )
            )
            .build()

        val response = dynamoDbClient.getItem(request)

        val item = (if (response.hasItem()) response.item() else null)
        return UserInformation(
            email = item?.get(EMAIL_ATTRIBUTE)?.s() ?: error("Missing email attribute")
        )
    }
}
