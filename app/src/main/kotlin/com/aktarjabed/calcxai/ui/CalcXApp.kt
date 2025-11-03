package com.aktarjabed.calcxai.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.aktarjabed.calcxai.ui.components.AppBottomNavigation
import com.aktarjabed.calcxai.ui.screens.AiAssistantScreen
import com.aktarjabed.calcxai.ui.screens.CalculatorScreen
import com.aktarjabed.calcxai.ui.screens.HistoryScreen

@Composable
fun CalcXApp() {
val navController = rememberNavController()

Scaffold(
bottomBar = { AppBottomNavigation(navController = navController) }
) { innerPadding ->
NavHost(
navController = navController,
startDestination = "assistant",
modifier = Modifier.padding(innerPadding)
) {
composable("assistant") { AiAssistantScreen() }
composable("calculator") { CalculatorScreen() }
composable("history") { HistoryScreen() }
}
}
}
