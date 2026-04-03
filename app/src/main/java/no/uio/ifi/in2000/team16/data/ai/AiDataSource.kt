package no.uio.ifi.in2000.team16.data.ai

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.json.Json
import no.uio.ifi.in2000.team16.BuildConfig
import javax.inject.Inject

class AiDataSource @Inject constructor(
    private val client: HttpClient
){
    private val AZURE_ENDPOINT = BuildConfig.AZURE_ENDPOINT
    private val AZURE_KEY = BuildConfig.AZURE_API_KEY
    private val MODEL_NAME = "gpt-5.4-nano"

    private val jsonParser = Json { ignoreUnknownKeys = true }

    suspend fun fetchEvaluation(prompt: String): AiEvaluationResponse {
        Log.d("T16_Ai_DataSource", "Fetching evaluation from AI: $prompt")
        val request = ChatRequest(
            model = MODEL_NAME,
            input = listOf(
                ChatMessage(
                    role = "user",
                    content = prompt
                )
            ),
            temperature = 0.2
        )
        return try{
            val response: ChatResponse = client.post(AZURE_ENDPOINT) {
                contentType(ContentType.Application.Json)
                header("api-key", AZURE_KEY)
                header("Authorization", "Bearer $AZURE_KEY")
                setBody(request)
            }.body()

            val aiResponseString = response.output[0].content[0].text
            Log.d("T16_Ai_DataSource", "Answer from AI: $aiResponseString")

            jsonParser.decodeFromString(aiResponseString)
        } catch (e: Exception) {
            Log.e("T16_Ai_DataSource", "Error in AiDataSource: ${e.message}", e)
            throw e
        }
    }
}