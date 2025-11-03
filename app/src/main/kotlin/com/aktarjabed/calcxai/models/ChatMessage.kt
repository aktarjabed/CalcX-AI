package com.aktarjabed.calcxai.models

data class ChatMessage(
    val text: String,
    val isUser: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)
