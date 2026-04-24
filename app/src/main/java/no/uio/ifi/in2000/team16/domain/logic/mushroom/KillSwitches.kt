package no.uio.ifi.in2000.team16.domain.logic.mushroom

/*
Similar to Scorers, these are the small building blocks that makes the algorithms.
What is common for all of them is that if any of them return a string (check is positive) it should make the score 0,
thereof the name killSwitch
*/

object SeasonKillSwitch {
    fun check(currentMonth: Int, activeMonths: List<Int>): String? {
        if (currentMonth !in activeMonths) return "Utenfor sesong"
        return null // If in correct month
    }
}

object FrostKillSwitch{
    fun check(minTempPast14Days: Double?, hardinessMinTemp: Double): String? {
        if (minTempPast14Days != null && minTempPast14Days < hardinessMinTemp) return "For kaldt"
        return null  // If there has been to cold temperatures
    }
}

object UrbanKillSwitch{
    fun check(arType: String?): String? {
        if (arType == null) return null
        if (arType != "Skog") return "Dette er ikke skog, så her vokser ingen sopp"
        return null   // If it is a forest
    }
}

