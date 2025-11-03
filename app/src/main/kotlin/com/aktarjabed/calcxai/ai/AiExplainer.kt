package com.aktarjabed.calcxai.ai

import com.aktarjabed.calcxai.models.CalculationType
import com.aktarjabed.calcxai.models.FinanceParams

class AiExplainer {

    fun generateExplanation(intent: CalculationType, params: FinanceParams): String {
        return when (intent) {
            CalculationType.SIP -> generateSIPExplanation(params)
            CalculationType.SWP -> generateSWPExplanation(params)
            CalculationType.EMI -> generateEMIExplanation(params)
            CalculationType.CAGR -> generateCAGRExplanation(params)
            CalculationType.LUMPSUM -> generateLumpsumExplanation(params)
            CalculationType.FD -> generateFDExplanation(params)
            CalculationType.RD -> generateRDExplanation(params)
            else -> "I'll help you calculate this."
        }
    }

    private fun generateSIPExplanation(params: FinanceParams): String {
        return """
        📈 **SIP Calculation**

        You're investing ₹${"%,.0f".format(params.monthlyInvestment)} monthly for ${params.years} years at ${params.rate}% annual return.

        **Formula Used:**
        Future Value = P × [((1 + r)ⁿ - 1) / r] × (1 + r)
        Where:
        P = Monthly Investment
        r = Monthly Rate (${params.rate}%/12)
        n = Total Months (${params.years * 12})

        This calculation assumes compounding monthly returns.
        """.trimIndent()
    }

    private fun generateSWPExplanation(params: FinanceParams): String {
        return """
        💰 **SWP Calculation**

        You're planning systematic withdrawals from your corpus for ${params.years} years.

        **Key Points:**
        • Initial Corpus: ₹${"%,.0f".format(params.corpus ?: 0)}
        • Annual Return Rate: ${params.rate}%
        • Withdrawal Period: ${params.years} years

        This helps you understand sustainable withdrawal rates from your investments.
        """.trimIndent()
    }

    private fun generateEMIExplanation(params: FinanceParams): String {
        return """
        🏠 **EMI Calculation**

        Loan Amount: ₹${"%,.0f".format(params.principal)}
        Interest Rate: ${params.rate}% per annum
        Tenure: ${params.years} years

        **EMI Formula:**
        EMI = [P × r × (1 + r)ⁿ] / [(1 + r)ⁿ - 1]
        Where:
        P = Principal Loan Amount
        r = Monthly Interest Rate
        n = Loan Tenure in Months
        """.trimIndent()
    }

    private fun generateCAGRExplanation(params: FinanceParams): String {
        return """
        📊 **CAGR Calculation**

        Measuring compound annual growth rate from ₹${"%,.0f".format(params.beginningValue)} to ₹${"%,.0f".format(params.endingValue)} over ${params.years} years.

        **CAGR Formula:**
        CAGR = (Ending Value / Beginning Value)^(1/Years) - 1

        This shows the smoothed annual growth rate of your investment.
        """.trimIndent()
    }

    private fun generateLumpsumExplanation(params: FinanceParams): String {
        return """
        💎 **Lumpsum Investment**

        One-time investment of ₹${"%,.0f".format(params.principal)} for ${params.years} years at ${params.rate}% annual return.

        **Future Value Formula:**
        FV = PV × (1 + r)ⁿ
        Where:
        PV = Present Value (Initial Investment)
        r = Annual Rate of Return
        n = Number of Years
        """.trimIndent()
    }

    private fun generateFDExplanation(params: FinanceParams): String {
        return """
        🏦 **Fixed Deposit**

        Fixed Deposit of ₹${"%,.0f".format(params.principal)} for ${params.years} years at ${params.rate}% interest.

        **Compound Interest Formula:**
        Maturity Amount = Principal × (1 + r/n)^(n×t)
        Where r is annual rate, n is compounding frequency (quarterly), t is years

        Note: This assumes quarterly compounding as per standard FD practices.
        """.trimIndent()
    }

    private fun generateRDExplanation(params: FinanceParams): String {
        return """
        🔄 **Recurring Deposit**

        Monthly investment of ₹${"%,.0f".format(params.monthlyInvestment)} for ${params.years} years at ${params.rate}% interest.

        **RD Formula:**
        Each installment compounds separately based on remaining tenure
        Maturity = Σ [Installment × (1 + r/4)^(quarters remaining)]
        Where r is annual interest rate

        Note: Standard RD compounds quarterly.
        """.trimIndent()
    }
}
