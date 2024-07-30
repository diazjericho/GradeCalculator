package com.mariejuana.gradecalculator.extensions

class Extensions {
    fun extractYears(input: String): Pair<Int, Int>? {
        val regex = Regex("(\\d{4})\\s*-\\s*(\\d{4})")
        val matchResult = regex.find(input)
        return matchResult?.destructured?.let { (year1, year2) ->
            year1.toInt() to year2.toInt()
        }
    }
}