package com.aktarjabed.calcxai.ui.viewmodels

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aktarjabed.calcxai.models.CalculationHistory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

class HistoryViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    fun loadHistory(context: Context) {
        viewModelScope.launch {
            try {
                val prefs = context.getSharedPreferences("calc_history", Context.MODE_PRIVATE)
                val historyJson = prefs.getString("history", "[]") ?: "[]"
                val jsonArray = JSONArray(historyJson)
                val historyList = mutableListOf<CalculationHistory>()

                for (i in 0 until jsonArray.length()) {
                    val item = jsonArray.getJSONObject(i)
                    historyList.add(
                        CalculationHistory(
                            type = item.getString("type"),
                            params = item.getString("params"),
                            result = item.getString("result"),
                            timestamp = item.getLong("timestamp")
                        )
                    )
                }

                _uiState.value = _uiState.value.copy(history = historyList)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun clearHistory(context: Context) {
        viewModelScope.launch {
            try {
                val prefs = context.getSharedPreferences("calc_history", Context.MODE_PRIVATE)
                prefs.edit().putString("history", "[]").apply()
                _uiState.value = _uiState.value.copy(history = emptyList())
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun shareResult(context: Context, item: CalculationHistory) {
        val shareText = """
            ${item.type} Calculation

            Parameters: ${item.params}
            Result: ${item.result}

            Calculated on: ${java.text.SimpleDateFormat("MMM dd, yyyy", java.util.Locale.getDefault()).format(java.util.Date(item.timestamp))}

            - CalcXAI App
        """.trimIndent()

        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, shareText)
        }

        context.startActivity(Intent.createChooser(shareIntent, "Share Calculation Result"))
    }
}

data class HistoryUiState(
    val history: List<CalculationHistory> = emptyList()
)
