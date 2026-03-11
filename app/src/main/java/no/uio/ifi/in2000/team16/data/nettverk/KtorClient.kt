package no.uio.ifi.in2000.team16.data.nettverk

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import javax.inject.Singleton

/*
    Objekt som oppretter en HttpKlient, slik at vi bare har én av denne.
 */

@Module
@InstallIn(SingletonComponent::class)
object KtorClient {
    @Provides
    @Singleton
    fun tilbyHttpClient(): HttpClient = HttpClient(Android) {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            })
        }
    }
}