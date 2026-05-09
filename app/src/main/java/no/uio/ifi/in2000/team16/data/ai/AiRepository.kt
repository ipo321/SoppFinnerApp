package no.uio.ifi.in2000.team16.data.ai

import no.uio.ifi.in2000.team16.domain.ai.AiService
import no.uio.ifi.in2000.team16.domain.model.EvaluationResult
import javax.inject.Inject

class AiRepository @Inject constructor(
    private val aiDataSource: AiDataSourceInterface
) : AiService {
    override suspend fun getAiScore(prompt: String): EvaluationResult {
        return try{
            val aiResponse = aiDataSource.fetchEvaluation(prompt)
            EvaluationResult(
                score = aiResponse.score,
                description = aiResponse.reason
            )
        } catch (e: Exception) {
            EvaluationResult(
                score = 0,
                description = "Evaluation from AI failed: ${e.message}"
            )
        }
    }
}