package no.uio.ifi.in2000.team16.data.ai

import kotlinx.serialization.Serializable

@Serializable
data class ChatRequest(
    val model: String,
    val input: List<ChatMessage>,
    val temperature: Double = 0.7,
    val max_tokens: Int = 200
)

@Serializable
data class ChatMessage(
    val role: String,
    val content: String
)

@Serializable
data class ChatResponse(
    val output: List<OutputItem>
)

@Serializable
data class OutputItem(
    val content: List<OutputContent>
)

@Serializable
data class OutputContent(
    val text: String
)

@Serializable
data class AiEvaluationResponse(
    val score: Int,
    val reason: String
)