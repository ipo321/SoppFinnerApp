package no.uio.ifi.in2000.team16.data.ai

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.json.Json
import no.uio.ifi.in2000.team16.BuildConfig
import javax.inject.Inject

interface AiDataSourceInterface {

    suspend fun fetchEvaluation(
        prompt: String
    ): AiEvaluationResponse
}

class AiDataSource @Inject constructor(
    private val client: HttpClient
) : AiDataSourceInterface {
    private val azureEndpoint = BuildConfig.AZURE_ENDPOINT.ifBlank {
        "https://missing-key-localy.no"   //
    }
    private val azureKey = BuildConfig.AZURE_API_KEY.ifBlank {
        "MISSING KEY"
    }
    private val modelName = "gpt-5.4-nano"

    private val jsonParser = Json { ignoreUnknownKeys = true }

    override suspend fun fetchEvaluation(prompt: String): AiEvaluationResponse {
        Log.d("T16_Ai_DataSource", "Fetching evaluation from AI: $prompt")
        val request = ChatRequest(
            model = modelName,
            input = listOf(
                ChatMessage(
                    role = "user",
                    content = prompt
                )
            ),
            temperature = 0.2
        )
        return try{
            val raw = client.post(azureEndpoint) {
                contentType(ContentType.Application.Json)
                header("api-key", azureKey)
                header("Authorization", "Bearer $azureKey")
                setBody(request)
            }.bodyAsText()

            Log.d("T16_Ai_DataSource", "AI raw: $raw")
            val response: ChatResponse = jsonParser.decodeFromString(raw)
            val aiResponseString = response.output[0].content[0].text
            Log.d("T16_Ai_DataSource", "Answer from AI: $aiResponseString")

            jsonParser.decodeFromString(aiResponseString)
        } catch (e: Exception) {
            Log.e("T16_Ai_DataSource", "Error in AiDataSource: ${e.message}", e)
            throw e
        }
    }
}