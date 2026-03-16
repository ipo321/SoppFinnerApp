package no.uio.ifi.in2000.team16.domain.logic

import no.uio.ifi.in2000.team16.domain.model.*

object SeasonKillSwitch {
    fun check(currentMonth: Int, activeMonths: List<Int>): String? {
        if (currentMonth !in activeMonths) return "Utenfor sesong"
        return null // If in correct month
    }
}

object FrostKillSwitch{
    fun check(temp:Double?, killTemp: Double): String? {
        if (temp != null && temp < killTemp) return "For kaldt"
        return null
    }
}

object SlopeKillSwitch{
    fun check(slopeDegree: Double?, maxSlopeDegree: Double): String? {
        if (slopeDegree == null) return null
        if (slopeDegree >= maxSlopeDegree) return "For bratt"
        return null
    }
}

object UrbanKillSwitch{
    fun check(arType: Int?): String? {
        if (arType == 11 || arType == 12) return "Ugyldig terreng"
        return null
    }
}
