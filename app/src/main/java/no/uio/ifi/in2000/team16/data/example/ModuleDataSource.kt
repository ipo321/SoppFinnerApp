package no.uio.ifi.in2000.team16.data.example

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import no.uio.ifi.in2000.team16.domain.model.ExampleClass
import javax.inject.Inject
import javax.inject.Singleton

/*
    Default example for API.
    Can be used to design API-calls (design is from the alpaca task)
*/

@Singleton
class ModuleDataSource @Inject constructor(
    private val httpClient : HttpClient
) {
    private val baseUrl = "https://in2000-proxy.ifi.uio.no/"

    suspend fun getModule(): List<ExampleClass> {
        val response: HttpResponse = httpClient.get("${baseUrl}/alpacaapi/v2/district1")
        if (response.status.value != 200) {
            throw Exception("Could not get module")
        }
        return response.body()
    }
}
