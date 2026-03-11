package no.uio.ifi.in2000.team16.data.modul

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import no.uio.ifi.in2000.team16.model.Modul
import javax.inject.Inject
import javax.inject.Singleton

/*
    En slags default om du vil, for å hente fra et API.
    Tanken er at denne kan kopieres/brukes som en slags mal på senere API-kall (om mulig).
    Alpakka-API-et er brukt for å teste.
*/

@Singleton
class ModulDataSource @Inject constructor(
    private val httpClient : HttpClient
) {
    private val baseUrl = "https://in2000-proxy.ifi.uio.no"

    suspend fun hentModul(): List<Modul> {
        val response: HttpResponse = httpClient.get("${baseUrl}/alpacaapi/v2/district1")
        if (response.status.value != 200) {
            throw Exception("Greide ikke å hente modul")
        }
        return response.body()
    }
}

@Module
@InstallIn(SingletonComponent::class)
object ModulApi {
    @Provides
    @Singleton
    fun tilbyModulDataSource(httpClient: HttpClient): ModulDataSource {
        return ModulDataSource(httpClient)
    }
}