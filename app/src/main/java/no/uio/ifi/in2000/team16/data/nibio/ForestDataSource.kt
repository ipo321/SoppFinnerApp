package no.uio.ifi.in2000.team16.data.nibio

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ForestDataSource @Inject constructor(
    private val httpClient : HttpClient
) {
    private val baseUrl = "https://wms.nibio.no/cgi-bin/ar5"

    suspend fun getForestInfo(query: String): String {
        val response: HttpResponse = httpClient.get("$baseUrl$query")
        if (response.status.value != 200) {
            throw Exception("Could not get forest info")
        }
        return response.body()
    }
}

@Module
@InstallIn(SingletonComponent::class)
object ForestApi {
    @Provides
    @Singleton
    fun provideForestDataSource(httpClient: HttpClient): ForestDataSource {
        return ForestDataSource(httpClient)
    }
}