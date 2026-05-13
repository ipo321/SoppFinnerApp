package no.uio.ifi.in2000.team16.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import no.uio.ifi.in2000.team16.data.ai.AiRepository
import no.uio.ifi.in2000.team16.domain.ai.AiService

@Suppress("unused")
@Module
@InstallIn(SingletonComponent::class)
abstract class AiModule {

    @Binds
    abstract fun bindAiService(
        aiServiceImpl: AiRepository
    ): AiService
}