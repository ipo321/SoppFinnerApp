package no.uio.ifi.in2000.team16.domain.logic.mushroom

import no.uio.ifi.in2000.team16.domain.ai.AiService
import no.uio.ifi.in2000.team16.domain.model.EnvironmentContext
import no.uio.ifi.in2000.team16.domain.model.EvaluationResult
import no.uio.ifi.in2000.team16.domain.model.SpeciesProfile
import javax.inject.Inject

class AiAlgorithm @Inject constructor(
    private val aiRepository: AiService
) : MushroomScorer {
    override suspend fun calculateScore(
        context: EnvironmentContext,
        species: SpeciesProfile
    ): EvaluationResult {
        val prompt = buildString {   //building the prompt in norwegian  to always geta norwegian answer
            append("Du er en presis ekspert på soppsanking i Norge. ")
            append("Din oppgave er å vurdere hvor god mulighet det er for å finne soppen '${species.name}'.\n\n")
            append("Dette skal du basere på denne dataen her i sin helhet, og husk at tidligere regn er positivt mens regn idag er negativt ")
            append("Her er miljødata for området:\n")
            append(context.toString())
            append("\n\n")

            append("REGLER DU MÅ FØLGE:\n")
            append("1. Hvis arealtypen (ar5) er Vann, Isbre, Snø, eller Bebygd, SKAL poengsummen være 0.\n")
            append("2. Bruk hele skalaen fra 0 til 100. Straff for feil temperatur, mangel på regn, eller feil skogtype. Ikke gi en gjennomsnittlig score hvis forholdene er dårlige.\n")
            append("3. Begrunnelsen SKAL være på maksimalt 1 kort, enkel setning. Rett på sak og på allmenntale uten. Begrunnelsen skal hjelpe folk med å forstå scoren\n\n")

            append("Svar KUN i dette JSON-formatet:\n")
            append("{\"score\": 85, \"reason\": \"Din korte begrunnelse her\"}")
        }
        return aiRepository.getAiScore(prompt)
    }
}