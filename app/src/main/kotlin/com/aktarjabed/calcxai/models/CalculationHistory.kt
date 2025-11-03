package com.aktarjabed.calcxai.models

data class CalculationHistory(
    val type: String,
    val params: String,
    val result: String,
    val timestamp: Long
)
