package com.aktarjabed.calcxai.models

data class FinanceParams(
    val monthlyInvestment: Double = 0.0,
    val principal: Double = 0.0,
    val corpus: Double? = null,
    val monthlyWithdrawal: Double = 0.0,
    val beginningValue: Double = 0.0,
    val endingValue: Double = 0.0,
    val years: Int = 0,
    val rate: Double = 0.0
)
