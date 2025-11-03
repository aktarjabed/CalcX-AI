package com.aktarjabed.calcxai.ai

import com.aktarjabed.calcxai.models.CalculationType

class IntentDetector {

    fun detectIntent(input: String): CalculationType {
        val lowerInput = input.lowercase()

        // Rule-based NLP with keyword matching
        return when {
            // SIP Detection
            containsAll(lowerInput, listOf("sip", "monthly", "invest")) -> CalculationType.SIP
            containsAny(lowerInput, listOf("systematic investment", "monthly investment")) -> CalculationType.SIP

            // SWP Detection
            containsAll(lowerInput, listOf("swp", "withdraw", "monthly")) -> CalculationType.SWP
            containsAny(lowerInput, listOf("systematic withdrawal", "monthly withdrawal")) -> CalculationType.SWP

            // EMI Detection
            containsAny(lowerInput, listOf("emi", "loan", "installment")) -> CalculationType.EMI

            // CAGR Detection
            containsAny(lowerInput, listOf("cagr", "annual growth", "compound growth")) -> CalculationType.CAGR

            // Lumpsum Detection
            containsAny(lowerInput, listOf("lumpsum", "one time", "lump sum")) -> CalculationType.LUMPSUM

            // FD Detection
            containsAny(lowerInput, listOf("fixed deposit", "fd", "term deposit")) -> CalculationType.FD

            // RD Detection
            containsAny(lowerInput, listOf("recurring deposit", "rd", "monthly deposit")) -> CalculationType.RD

            // Basic math
            containsAny(lowerInput, listOf("+", "-", "*", "/", "calculate", "what is")) -> CalculationType.BASIC

            else -> CalculationType.BASIC
        }
    }

    private fun containsAny(input: String, keywords: List<String>): Boolean {
        return keywords.any { keyword ->
            input.contains(keyword, ignoreCase = true)
        }
    }

    private fun containsAll(input: String, keywords: List<String>): Boolean {
        return keywords.all { keyword ->
            input.contains(keyword, ignoreCase = true)
        }
    }
}
