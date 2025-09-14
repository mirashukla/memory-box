package org.mira.dagger

import dagger.Component
import org.mira.lambda.MemoryHandler
import javax.inject.Singleton

@Singleton
@Component(
    modules = [AwsModule::class]
)

interface AppComponent {
    fun inject(handler: MemoryHandler)
}