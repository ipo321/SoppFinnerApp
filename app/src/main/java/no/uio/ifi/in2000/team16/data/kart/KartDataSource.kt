package no.uio.ifi.in2000.team16.data.kart

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import no.uio.ifi.in2000.team16.model.Kart
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class KartDataSource @Inject constructor(
    private val httpClient : HttpClient
) {
    private val baseUrl = "https://in2000-proxy.ifi.uio.no"

    suspend fun hentKart(): List<Kart> {
        val response: HttpResponse = httpClient.get("${baseUrl}/alpacaapi/v2/district1")
        if (response.status.value != 200) {
            throw Exception("Greide ikke å hente kart")
        }
        return response.body()
    }
}

@Module
@InstallIn(SingletonComponent::class)
object KartApi {
    @Provides
    @Singleton
    fun tilbyKartDataSource(httpClient: HttpClient): KartDataSource {
        return KartDataSource(httpClient)
    }
}