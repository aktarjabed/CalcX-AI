package com.aktarjabed.calcxai.ai

import android.content.Context
import android.util.Log
import com.aktarjabed.calcxai.models.CalculationType
import com.aktarjabed.calcxai.models.FinanceParams
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.regex.Pattern

class AiProcessor(private val context: Context) {

    private val intentDetector = IntentDetector()
    private val financeParser = FinanceParser()
    private val aiExplainer = AiExplainer()

    suspend fun processUserInput(input: String): AiResult {
        return withContext(Dispatchers.Default) {
            try {
                // Step 1: Detect intent using hybrid approach
                val intent = intentDetector.detectIntent(input)

                // Step 2: Extract financial parameters
                val params = financeParser.extractFinanceParams(input, intent)

                // Step 3: Generate explanation
                val explanation = aiExplainer.generateExplanation(intent, params)

                AiResult(
                    intent = intent,
                    params = params,
                    explanation = explanation,
                    success = true
                )
            } catch (e: Exception) {
                Log.e("AiProcessor", "Error processing input: ${e.message}")
                AiResult(
                    intent = CalculationType.BASIC,
                    params = FinanceParams(),
                    explanation = "I couldn't understand your request. Please try rephrasing with specific numbers.\n\nExample: 'Calculate SIP of ₹5000 monthly for 10 years at 12% return'",
                    success = false
                )
            }
        }
    }

    fun calculateResult(intent: CalculationType, params: FinanceParams): CalculationResult {
        return try {
            when (intent) {
                CalculationType.SIP -> calculateSIP(params)
                CalculationType.SWP -> calculateSWP(params)
                CalculationType.EMI -> calculateEMI(params)
                CalculationType.CAGR -> calculateCAGR(params)
                CalculationType.LUMPSUM -> calculateLumpsum(params)
                CalculationType.FD -> calculateFD(params)
                CalculationType.RD -> calculateRD(params)
                else -> calculateBasic(params)
            }
        } catch (e: Exception) {
            Log.e("AiProcessor", "Calculation error: ${e.message}")
            CalculationResult(0.0, 0.0, 0.0, 0.0)
        }
    }

    private fun calculateSIP(params: FinanceParams): CalculationResult {
        val monthlyRate = params.rate / 12 / 100
        val months = params.years * 12
        val futureValue = params.monthlyInvestment *
            ((Math.pow(1 + monthlyRate, months.toDouble()) - 1) / monthlyRate) *
            (1 + monthlyRate)

        val totalInvestment = params.monthlyInvestment * months
        val wealthGained = futureValue - totalInvestment

        return CalculationResult(
            finalAmount = futureValue,
            totalInvestment = totalInvestment,
            wealthGained = wealthGained,
            monthlyValue = params.monthlyInvestment
        )
    }

    private fun calculateSWP(params: FinanceParams): CalculationResult {
        val monthlyRate = params.rate / 12 / 100
        val months = params.years * 12
        val corpus = params.corpus ?: 0.0

        val monthlyWithdrawal = if (params.monthlyWithdrawal > 0) {
            params.monthlyWithdrawal
        } else {
            // Calculate sustainable withdrawal
            corpus * monthlyRate / (1 - Math.pow(1 + monthlyRate, -months.toDouble()))
        }

        // Calculate remaining corpus
        var remainingCorpus = corpus
        for (i in 1..months) {
            remainingCorpus = remainingCorpus * (1 + monthlyRate) - monthlyWithdrawal
            if (remainingCorpus < 0) {
                remainingCorpus = 0.0
                break
            }
        }

        return CalculationResult(
            finalAmount = remainingCorpus,
            totalInvestment = corpus,
            wealthGained = monthlyWithdrawal * months - corpus,
            monthlyValue = monthlyWithdrawal
        )
    }

    private fun calculateEMI(params: FinanceParams): CalculationResult {
        val monthlyRate = params.rate / 12 / 100
        val months = params.years * 12
        val principal = params.principal

        val emi = principal * monthlyRate *
            Math.pow(1 + monthlyRate, months.toDouble()) /
            (Math.pow(1 + monthlyRate, months.toDouble()) - 1)

        val totalPayment = emi * months
        val totalInterest = totalPayment - principal

        return CalculationResult(
            finalAmount = totalPayment,
            totalInvestment = principal,
            wealthGained = totalInterest,
            monthlyValue = emi
        )
    }

    private fun calculateCAGR(params: FinanceParams): CalculationResult {
        val beginningValue = params.beginningValue
        val endingValue = params.endingValue
        val years = params.years.toDouble()

        val cagr = (Math.pow(endingValue / beginningValue, 1.0 / years) - 1) * 100

        return CalculationResult(
            finalAmount = cagr,
            totalInvestment = beginningValue,
            wealthGained = endingValue - beginningValue,
            monthlyValue = 0.0
        )
    }

    private fun calculateLumpsum(params: FinanceParams): CalculationResult {
        val futureValue = params.principal *
            Math.pow(1 + params.rate / 100, params.years.toDouble())

        return CalculationResult(
            finalAmount = futureValue,
            totalInvestment = params.principal,
            wealthGained = futureValue - params.principal,
            monthlyValue = 0.0
        )
    }

    private fun calculateFD(params: FinanceParams): CalculationResult {
        val principal = params.principal
        val rate = params.rate
        val years = params.years

        // Compound interest quarterly (typical for FDs)
        val quarterlyRate = rate / 4
        val quarters = years * 4
        val maturityAmount = principal * Math.pow(1 + quarterlyRate/100, quarters.toDouble())
        val interestEarned = maturityAmount - principal

        return CalculationResult(
            finalAmount = maturityAmount,
            totalInvestment = principal,
            wealthGained = interestEarned,
            monthlyValue = 0.0
        )
    }

    private fun calculateRD(params: FinanceParams): CalculationResult {
        val monthlyInvestment = params.monthlyInvestment
        val rate = params.rate
        val years = params.years

        val months = years * 12
        val quarterlyRate = rate / 4
        var maturityAmount = 0.0

        // RD calculation with quarterly compounding
        for (i in 1..months) {
            val quartersRemaining = (months - i + 1) / 3.0
            maturityAmount += monthlyInvestment * Math.pow(1 + quarterlyRate/100, quartersRemaining)
        }

        val totalInvestment = monthlyInvestment * months
        val interestEarned = maturityAmount - totalInvestment

        return CalculationResult(
            finalAmount = maturityAmount,
            totalInvestment = totalInvestment,
            wealthGained = interestEarned,
            monthlyValue = monthlyInvestment
        )
    }

    private fun calculateBasic(params: FinanceParams): CalculationResult {
        return CalculationResult(
            finalAmount = 0.0,
            totalInvestment = 0.0,
            wealthGained = 0.0,
            monthlyValue = 0.0
        )
    }
}

data class AiResult(
    val intent: CalculationType,
    val params: FinanceParams,
    val explanation: String,
    val success: Boolean
)

data class CalculationResult(
    val finalAmount: Double,
    val totalInvestment: Double,
    val wealthGained: Double,
    val monthlyValue: Double
)
