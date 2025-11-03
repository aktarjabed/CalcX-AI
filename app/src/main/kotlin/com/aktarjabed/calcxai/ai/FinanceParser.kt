package com.aktarjabed.calcxai.ai

import com.aktarjabed.calcxai.models.CalculationType
import com.aktarjabed.calcxai.models.FinanceParams
import java.util.regex.Pattern

class FinanceParser {

    fun extractFinanceParams(input: String, intent: CalculationType): FinanceParams {
        return when (intent) {
            CalculationType.SIP -> extractSIPParams(input)
            CalculationType.SWP -> extractSWPParams(input)
            CalculationType.EMI -> extractEMIParams(input)
            CalculationType.CAGR -> extractCAGRParams(input)
            CalculationType.LUMPSUM -> extractLumpsumParams(input)
            CalculationType.FD -> extractFDParams(input)
            CalculationType.RD -> extractRDParams(input)
            else -> FinanceParams() // Basic math params
        }
    }

    private fun extractSIPParams(input: String): FinanceParams {
        return FinanceParams(
            monthlyInvestment = extractAmountAfterKeyword(input, listOf("invest", "monthly", "sip")),
            years = extractYears(input),
            rate = extractRate(input)
        )
    }

    private fun extractSWPParams(input: String): FinanceParams {
        return FinanceParams(
            corpus = extractAmountAfterKeyword(input, listOf("corpus", "amount", "withdraw")),
            monthlyWithdrawal = extractAmountAfterKeyword(input, listOf("withdraw", "monthly")),
            years = extractYears(input),
            rate = extractRate(input)
        )
    }

    private fun extractEMIParams(input: String): FinanceParams {
        return FinanceParams(
            principal = extractAmountAfterKeyword(input, listOf("loan", "principal", "amount")),
            years = extractYears(input),
            rate = extractRate(input)
        )
    }

    private fun extractCAGRParams(input: String): FinanceParams {
        val numbers = extractAllNumbers(input)
        return FinanceParams(
            beginningValue = if (numbers.size >= 1) numbers[0] else 0.0,
            endingValue = if (numbers.size >= 2) numbers[1] else 0.0,
            years = if (numbers.size >= 3) numbers[2].toInt() else extractYears(input)
        )
    }

    private fun extractLumpsumParams(input: String): FinanceParams {
        return FinanceParams(
            principal = extractAmountAfterKeyword(input, listOf("invest", "amount", "principal")),
            years = extractYears(input),
            rate = extractRate(input)
        )
    }

    private fun extractFDParams(input: String): FinanceParams {
        return FinanceParams(
            principal = extractAmountAfterKeyword(input, listOf("deposit", "amount", "principal")),
            years = extractYears(input),
            rate = extractRate(input)
        )
    }

    private fun extractRDParams(input: String): FinanceParams {
        return FinanceParams(
            monthlyInvestment = extractAmountAfterKeyword(input, listOf("monthly", "invest", "deposit")),
            years = extractYears(input),
            rate = extractRate(input)
        )
    }

    private fun extractAmountAfterKeyword(input: String, keywords: List<String>): Double {
        val lowerInput = input.lowercase()
        for (keyword in keywords) {
            val index = lowerInput.indexOf(keyword)
            if (index != -1) {
                val afterKeyword = input.substring(index + keyword.length)
                val numbers = extractAllNumbers(afterKeyword)
                if (numbers.isNotEmpty()) {
                    return numbers[0]
                }
            }
        }
        return extractFirstNumber(input) ?: 0.0
    }

    private fun extractRate(input: String): Double {
        val pattern = Pattern.compile("(\\d+(?:\\.\\d+)?)\\s*%")
        val matcher = pattern.matcher(input)
        if (matcher.find()) {
            return matcher.group(1).toDouble()
        }
        return extractFirstNumber(input) ?: 8.0 // Default rate
    }

    private fun extractYears(input: String): Int {
        val pattern = Pattern.compile("(\\d+)\\s*(?:years|yrs|year|y)")
        val matcher = pattern.matcher(input.lowercase())
        if (matcher.find()) {
            return matcher.group(1).toInt()
        }
        return extractFirstNumber(input)?.toInt() ?: 5 // Default years
    }

    private fun extractFirstNumber(input: String): Double? {
        val pattern = Pattern.compile("\\d+(?:\\.\\d+)?")
        val matcher = pattern.matcher(input)
        return if (matcher.find()) {
            matcher.group().toDouble()
        } else {
            null
        }
    }

    private fun extractAllNumbers(input: String): List<Double> {
        val pattern = Pattern.compile("\\d+(?:\\.\\d+)?")
        val matcher = pattern.matcher(input)
        val numbers = mutableListOf<Double>()
        while (matcher.find()) {
            numbers.add(matcher.group().toDouble())
        }
        return numbers
    }
}
