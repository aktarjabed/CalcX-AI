package com.aktarjabed.calcxai.ui.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aktarjabed.calcxai.ai.AiProcessor
import com.aktarjabed.calcxai.ai.CalculationResult
import com.aktarjabed.calcxai.models.CalculationType
import com.aktarjabed.calcxai.models.CalculationHistory
import com.aktarjabed.calcxai.models.FinanceParams
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CalculatorViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(CalculatorUiState())
    val uiState: StateFlow<CalculatorUiState> = _uiState.asStateFlow()

    fun updateCalculationType(type: CalculationType) {
        _uiState.value = _uiState.value.copy(
            selectedCalcType = type,
            params = FinanceParams(),
            result = null
        )
    }

    fun updateParams(params: FinanceParams) {
        _uiState.value = _uiState.value.copy(params = params)
    }

    fun calculate(context: Context) {
        viewModelScope.launch {
            try {
                val aiProcessor = AiProcessor(context)
                val result = aiProcessor.calculateResult(_uiState.value.selectedCalcType, _uiState.value.params)
                _uiState.value = _uiState.value.copy(result = result)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun saveToHistory(context: Context) {
        viewModelScope.launch {
            try {
                val result = _uiState.value.result ?: return@launch
                val params = _uiState.value.params
                val type = _uiState.value.selectedCalcType

                val paramsString = when (type) {
                    CalculationType.SIP -> "Monthly: ₹${params.monthlyInvestment}, Years: ${params.years}, Rate: ${params.rate}%"
                    CalculationType.SWP -> "Corpus: ₹${params.corpus}, Years: ${params.years}, Rate: ${params.rate}%"
                    CalculationType.EMI -> "Principal: ₹${params.principal}, Years: ${params.years}, Rate: ${params.rate}%"
                    CalculationType.CAGR -> "Beginning: ₹${params.beginningValue}, Ending: ₹${params.endingValue}, Years: ${params.years}"
                    CalculationType.LUMPSUM -> "Amount: ₹${params.principal}, Years: ${params.years}, Rate: ${params.rate}%"
                    CalculationType.FD -> "Amount: ₹${params.principal}, Years: ${params.years}, Rate: ${params.rate}%"
                    CalculationType.RD -> "Monthly: ₹${params.monthlyInvestment}, Years: ${params.years}, Rate: ${params.rate}%"
                    else -> ""
                }

                val resultString = when (type) {
                    CalculationType.CAGR -> "${String.format("%.2f", result.finalAmount)}%"
                    else -> "₹${String.format("%,.2f", result.finalAmount)}"
                }

                val historyItem = CalculationHistory(
                    type = type.name,
                    params = paramsString,
                    result = resultString,
                    timestamp = System.currentTimeMillis()
                )

                // Save to SharedPreferences or Room DB
                saveHistoryItem(context, historyItem)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    private fun saveHistoryItem(context: Context, item: CalculationHistory) {
        // Simple implementation using SharedPreferences
        // In a real app, you would use Room database
        val prefs = context.getSharedPreferences("calc_history", Context.MODE_PRIVATE)
        val historyJson = prefs.getString("history", "[]") ?: "[]"

        // This is a simplified approach - in production use Room
        // For now, we'll just show a toast message in the UI
    }
}

data class CalculatorUiState(
    val selectedCalcType: CalculationType = CalculationType.SIP,
    val params: FinanceParams = FinanceParams(),
    val result: CalculationResult? = null
)
