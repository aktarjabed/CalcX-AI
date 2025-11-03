package com.aktarjabed.calcxai.ui.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aktarjabed.calcxai.ai.AiProcessor
import com.aktarjabed.calcxai.ai.AiResult
import com.aktarjabed.calcxai.ai.CalculationResult
import com.aktarjabed.calcxai.models.ChatMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AiAssistantViewModel : ViewModel() {

private val _uiState = MutableStateFlow(AiAssistantUiState())
val uiState: StateFlow

private lateinit var aiProcessor: AiProcessor

fun initialize(context: Context) {
aiProcessor = AiProcessor(context)
addMessage(
ChatMessage(
text = "👋 Hello! I'm your AI finance assistant. I can help you with:\n\n" +
"• 💰 SIP Calculations\n• 💸 SWP Planning\n• 🏠 EMI Calculations\n• 📈 CAGR Analysis\n" +
"• 💎 Lumpsum Investments\n• 🏦 Fixed Deposits\n• 🔄 Recurring Deposits\n\n" +
"💡 Try asking: 'Calculate SIP of ₹5000 monthly for 10 years at 12% return'",
isUser = false
)
)
}

fun updateUserInput(input: String) {
_uiState.value = _uiState.value.copy(userInput = input)
}

fun processUserInput() {
val input = _uiState.value.userInput.trim()
if (input.isEmpty()) return

addMessage(ChatMessage(text = input, isUser = true))
_uiState.value = _uiState.value.copy(userInput = "", isProcessing = true)

viewModelScope.launch {
try {
val result = aiProcessor.processUserInput(input)
if (result.success) {
val calculation = aiProcessor.calculateResult(result.intent, result.params)
val response = formatAiResponse(result, calculation)
addMessage(ChatMessage(text = response, isUser = false))
} else {
addMessage(ChatMessage(text = result.explanation, isUser = false))
}
} catch (e: Exception) {
addMessage(ChatMessage(
text = "❌ Sorry, I encountered an error: ${e.message}\n\nPlease try rephrasing your question.",
isUser = false
))
} finally {
_uiState.value = _uiState.value.copy(isProcessing = false)
}
}
}

fun addMessage(text: String, isUser: Boolean = false) {
addMessage(ChatMessage(text = text, isUser = isUser))
}

private fun addMessage(message: ChatMessage) {
val currentMessages = _uiState.value.messages.toMutableList()
currentMessages.add(message)
_uiState.value = _uiState.value.copy(messages = currentMessages)
}

private fun formatAiResponse(aiResult: AiResult, calculation: CalculationResult): String {
return """
${aiResult.explanation}

📊 **Calculation Results:**
• Final Amount: ₹${"%,.2f".format(calculation.finalAmount)}
• Total Investment: ₹${"%,.2f".format(calculation.totalInvestment)}
• Wealth Gained: ₹${"%,.2f".format(calculation.wealthGained)}
${if (calculation.monthlyValue > 0) "• Monthly Amount: ₹${"%,.2f".format(calculation.monthlyValue)}" else ""}

💡 *Note: These are approximate values for educational purposes. Actual returns may vary.*
""".trimIndent()
}
}

data class AiAssistantUiState(
val userInput: String = "",
val messages: List<ChatMessage> = emptyList(),
val isProcessing: Boolean = false
)
