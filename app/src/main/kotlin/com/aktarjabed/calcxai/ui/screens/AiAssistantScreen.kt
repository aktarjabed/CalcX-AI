package com.aktarjabed.calcxai.ui.screens

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aktarjabed.calcxai.ai.SpeechInputManager
import com.aktarjabed.calcxai.ui.components.ChatBubble
import com.aktarjabed.calcxai.ui.viewmodels.AiAssistantViewModel
import kotlinx.coroutines.launch

@Composable
fun AiAssistantScreen() {
val viewModel: AiAssistantViewModel = viewModel()
val state by viewModel.uiState.collectAsState()
val context = LocalContext.current
val coroutineScope = rememberCoroutineScope()

LaunchedEffect(Unit) {
viewModel.initialize(context)
}

val speechManager = remember { SpeechInputManager(context) }
val isListening by speechManager.isListening.collectAsState()

val permissionLauncher = rememberLauncherForActivityResult(
ActivityResultContracts.RequestPermission()
) { granted ->
if (granted) {
coroutineScope.launch {
speechManager.startListening(
onResult = { result ->
viewModel.updateUserInput(result)
viewModel.processUserInput()
},
onError = { error ->
viewModel.addMessage("🎤 Speech Error: $error", isUser = false)
}
)
}
} else {
viewModel.addMessage("❌ Microphone permission is required for voice input", isUser = false)
}
}

Column(
modifier = Modifier
.fillMaxSize()
.padding(16.dp)
) {
Text(
text = "AI Finance Assistant",
style = MaterialTheme.typography.headlineMedium,
modifier = Modifier.padding(bottom = 16.dp)
)

LazyColumn(
modifier = Modifier
.weight(1f)
.fillMaxWidth(),
verticalArrangement = Arrangement.spacedBy(8.dp),
contentPadding = PaddingValues(bottom = 8.dp)
) {
items(state.messages) { message ->
ChatBubble(message = message, modifier = Modifier.padding(vertical = 4.dp))
}
}

Row(
modifier = Modifier
.fillMaxWidth()
.padding(top = 8.dp),
verticalAlignment = Alignment.CenterVertically
) {
OutlinedTextField(
value = state.userInput,
onValueChange = { viewModel.updateUserInput(it) },
placeholder = { Text("Ask me anything about finance...") },
modifier = Modifier
.weight(1f)
.padding(end = 8.dp),
shape = RoundedCornerShape(24.dp),
enabled = !state.isProcessing && !isListening
)

IconButton(
onClick = {
if (isListening) {
speechManager.stopListening()
} else {
permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
}
},
enabled = !state.isProcessing
) {
Icon(
imageVector = Icons.Default.Mic,
contentDescription = "Voice input",
tint = if (isListening) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
)
}

IconButton(
onClick = { viewModel.processUserInput() },
enabled = state.userInput.isNotEmpty() && !state.isProcessing && !isListening
) {
Icon(
imageVector = Icons.Default.Send,
contentDescription = "Send",
tint = MaterialTheme.colorScheme.primary
)
}
}

if (state.isProcessing || isListening) {
LinearProgressIndicator(
modifier = Modifier
.fillMaxWidth()
.padding(top = 8.dp)
)
Text(
text = if (isListening) "🎤 Listening..." else "🤔 Processing...",
style = MaterialTheme.typography.bodySmall,
modifier = Modifier
.fillMaxWidth()
.padding(top = 4.dp),
textAlign = TextAlign.Center
)
}
}
}
