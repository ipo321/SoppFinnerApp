package no.uio.ifi.in2000.team16.domain.ai

import no.uio.ifi.in2000.team16.domain.model.EvaluationResult

/*
Interface for communication between the AI service and the domain layer
 */


interface AiService {
    suspend fun getAiScore(prompt: String): EvaluationResult
}