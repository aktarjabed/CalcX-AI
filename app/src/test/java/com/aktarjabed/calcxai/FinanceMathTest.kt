package com.aktarjabed.calcxai

import com.aktarjabed.calcxai.ai.AiProcessor
import com.aktarjabed.calcxai.models.CalculationType
import com.aktarjabed.calcxai.models.FinanceParams
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.kotlin.mock

class FinanceMathTest {

    private val aiProcessor = AiProcessor(context = mock())

    @Test
    fun sip_monthly_compounding_matches_formula() {
        val params = FinanceParams(
            monthlyInvestment = 5000.0,
            rate = 12.0,
            years = 10.0
        )
        val result = aiProcessor.calculateResult(CalculationType.SIP, params)
        assertEquals(1161695.2, result.finalAmount, 0.1)
    }

    @Test
    fun swp_never_drops_below_zero_balance() {
        val params = FinanceParams(
            corpus = 100000.0,
            monthlyWithdrawal = 1000.0,
            rate = 8.0,
            years = 10.0
        )
        val result = aiProcessor.calculateResult(CalculationType.SWP, params)
        assert(result.finalAmount >= 0)
    }

    @Test
    fun fd_quarterly_compounding_matches_bank_standard() {
        val params = FinanceParams(
            principal = 100000.0,
            rate = 6.0,
            years = 5.0
        )
        val result = aiProcessor.calculateResult(CalculationType.FD, params)
        assertEquals(134685.5, result.finalAmount, 0.1)
    }

    @Test
    fun invalid_inputs_return_failure() {
        val params = FinanceParams(
            monthlyInvestment = -100.0,
            rate = 12.0,
            years = 10.0
        )
        val result = aiProcessor.calculateResult(CalculationType.SIP, params)
        assertEquals(0.0, result.finalAmount, 0.1)
    }
}
