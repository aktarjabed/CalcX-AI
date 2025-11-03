package com.aktarjabed.calcxai.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.aktarjabed.calcxai.ai.CalculationResult
import com.aktarjabed.calcxai.models.CalculationType

@Composable
fun CalculationResultCard(
    result: CalculationResult,
    calcType: CalculationType,
    onSave: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Calculation Results",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            Spacer(modifier = Modifier.height(16.dp))

            ResultRow(
                label = getResultLabel(calcType, "final"),
                value = "₹${String.format("%,.2f", result.finalAmount)}"
            )

            ResultRow(
                label = getResultLabel(calcType, "investment"),
                value = "₹${String.format("%,.2f", result.totalInvestment)}"
            )

            ResultRow(
                label = getResultLabel(calcType, "gains"),
                value = "₹${String.format("%,.2f", result.wealthGained)}",
                valueColor = if (result.wealthGained >= 0)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.error
            )

            if (result.monthlyValue > 0) {
                ResultRow(
                    label = getResultLabel(calcType, "monthly"),
                    value = "₹${String.format("%,.2f", result.monthlyValue)}"
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onSave,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Bookmark,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Save")
                }

                Button(
                    onClick = { /* TODO: Implement share */ },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Share")
                }
            }
        }
    }
}

@Composable
fun ResultRow(
    label: String,
    value: String,
    valueColor: Color = MaterialTheme.colorScheme.onPrimaryContainer
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = valueColor
        )
    }
}

private fun getResultLabel(calcType: CalculationType, type: String): String {
    return when (calcType) {
        CalculationType.SIP -> when (type) {
            "final" -> "Future Value"
            "investment" -> "Total Investment"
            "gains" -> "Wealth Gained"
            "monthly" -> "Monthly Investment"
            else -> ""
        }
        CalculationType.SWP -> when (type) {
            "final" -> "Remaining Corpus"
            "investment" -> "Initial Corpus"
            "gains" -> "Total Withdrawn"
            "monthly" -> "Monthly Withdrawal"
            else -> ""
        }
        CalculationType.EMI -> when (type) {
            "final" -> "Total Payment"
            "investment" -> "Principal Amount"
            "gains" -> "Total Interest"
            "monthly" -> "Monthly EMI"
            else -> ""
        }
        CalculationType.CAGR -> when (type) {
            "final" -> "CAGR (%)"
            "investment" -> "Beginning Value"
            "gains" -> "Total Gain"
            else -> ""
        }
        CalculationType.LUMPSUM -> when (type) {
            "final" -> "Future Value"
            "investment" -> "Investment Amount"
            "gains" -> "Wealth Gained"
            else -> ""
        }
        CalculationType.FD -> when (type) {
            "final" -> "Maturity Amount"
            "investment" -> "Deposit Amount"
            "gains" -> "Interest Earned"
            else -> ""
        }
        CalculationType.RD -> when (type) {
            "final" -> "Maturity Amount"
            "investment" -> "Total Investment"
            "gains" -> "Interest Earned"
            "monthly" -> "Monthly Investment"
            else -> ""
        }
        else -> ""
    }
}
