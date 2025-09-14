package org.mira.dagger

import dagger.Module
import dagger.Provides
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import javax.inject.Singleton

@Module
object AwsModule {
    @Provides
    @Singleton
    fun dynamoDbClient(): DynamoDbClient =
        DynamoDbClient.create()
}