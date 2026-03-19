package no.uio.ifi.in2000.team16.domain.logic.mushroom

/*
Similar to Scorers, these are the small building blocks that makes the algorithms. What is common for all of them is that if any of them return a string (check is positive) it should make the
the score 0, thereof the name killSwitch
*/

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
        if (arType == null) return null

        return when (arType){
            11 -> "Bebyggelse."
            12 -> "Vei / Samferdsel."
            21, 22 -> "Dyrket mark"
            70 -> "Isbre / Snø."
            81, 82 -> "Vann / Hav."
            else -> null
        }
    }
}
