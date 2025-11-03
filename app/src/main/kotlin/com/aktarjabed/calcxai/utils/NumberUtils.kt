package com.aktarjabed.calcxai.utils

import java.text.NumberFormat
import java.util.*

object NumberUtils {
    fun formatCurrency(amount: Double): String {
        val format = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
        return format.format(amount)
    }

    fun formatPercentage(value: Double): String {
        return String.format("%.2f%%", value)
    }

    fun formatNumber(value: Double): String {
        return String.format("%,.2f", value)
    }
}
