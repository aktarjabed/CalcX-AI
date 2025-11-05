package com.aktarjabed.calcxai.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aktarjabed.calcxai.ai.AiProcessor
import com.aktarjabed.calcxai.models.CalculationType
import com.aktarjabed.calcxai.models.FinanceParams
import com.aktarjabed.calcxai.ui.components.CalculationResultCard
import com.aktarjabed.calcxai.ui.viewmodels.CalculatorViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorScreen() {
    val viewModel: CalculatorViewModel = viewModel()
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Financial Calculators",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Calculator type selector
        CalculationTypeSelector(
            selectedType = state.selectedCalcType,
            onTypeSelected = { viewModel.updateCalculationType(it) }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Dynamic calculator form based on selected type
        when (state.selectedCalcType) {
            CalculationType.SIP -> SIPCalculatorForm(
                params = state.params,
                onParamsChange = { viewModel.updateParams(it) },
                onCalculate = { viewModel.calculate(context) }
            )
            CalculationType.SWP -> SWPCalculatorForm(
                params = state.params,
                onParamsChange = { viewModel.updateParams(it) },
                onCalculate = { viewModel.calculate(context) }
            )
            CalculationType.EMI -> EMICalculatorForm(
                params = state.params,
                onParamsChange = { viewModel.updateParams(it) },
                onCalculate = { viewModel.calculate(context) }
            )
            CalculationType.CAGR -> CAGRCalculatorForm(
                params = state.params,
                onParamsChange = { viewModel.updateParams(it) },
                onCalculate = { viewModel.calculate(context) }
            )
            CalculationType.LUMPSUM -> LumpsumCalculatorForm(
                params = state.params,
                onParamsChange = { viewModel.updateParams(it) },
                onCalculate = { viewModel.calculate(context) }
            )
            CalculationType.FD -> FDCalculatorForm(
                params = state.params,
                onParamsChange = { viewModel.updateParams(it) },
                onCalculate = { viewModel.calculate(context) }
            )
            CalculationType.RD -> RDCalculatorForm(
                params = state.params,
                onParamsChange = { viewModel.updateParams(it) },
                onCalculate = { viewModel.calculate(context) }
            )
            else -> BasicCalculatorForm()
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Display result if available
        state.result?.let { result ->
            CalculationResultCard(
                result = result,
                calcType = state.selectedCalcType,
                onSave = { viewModel.saveToHistory(context) }
            )
        }
    }
}

@Composable
fun CalculationTypeSelector(
    selectedType: CalculationType,
    onTypeSelected: (CalculationType) -> Unit
) {
    val calculationTypes = listOf(
        CalculationType.SIP to "SIP",
        CalculationType.SWP to "SWP",
        CalculationType.EMI to "EMI",
        CalculationType.CAGR to "CAGR",
        CalculationType.LUMPSUM to "Lumpsum",
        CalculationType.FD to "FD",
        CalculationType.RD to "RD"
    )

    Column {
        Text(
            text = "Select Calculator Type:",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Use a scrollable row for the chips
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            calculationTypes.forEach { (type, label) ->
                FilterChip(
                    selected = selectedType == type,
                    onClick = { onTypeSelected(type) },
                    label = { Text(label) },
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }
    }
}

import com.aktarjabed.calcxai.ui.components.LabeledTextField
@Composable
fun SIPCalculatorForm(
    params: FinanceParams,
    onParamsChange: (FinanceParams) -> Unit,
    onCalculate: () -> Unit
) {
    val context = LocalContext.current
    var monthlyInvestment by remember { mutableStateOf(if (params.monthlyInvestment > 0) params.monthlyInvestment.toString() else "") }
    var years by remember { mutableStateOf(if (params.years > 0) params.years.toString() else "") }
    var rate by remember { mutableStateOf(if (params.rate > 0) params.rate.toString() else "") }

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "SIP Calculator",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        LabeledTextField(
            label = "Monthly Investment (₹)",
            text = monthlyInvestment,
            onTextChange = {
                monthlyInvestment = it
                onParamsChange(params.copy(monthlyInvestment = it.toDoubleOrNull() ?: 0.0))
            },
            keyboardType = KeyboardType.Number
        )

        LabeledTextField(
            label = "Investment Period (Years)",
            text = years,
            onTextChange = {
                years = it
                onParamsChange(params.copy(years = it.toIntOrNull() ?: 0))
            },
            keyboardType = KeyboardType.Number
        )

        LabeledTextField(
            label = "Expected Return Rate (%)",
            text = rate,
            onTextChange = {
                rate = it
                onParamsChange(params.copy(rate = it.toDoubleOrNull() ?: 0.0))
            },
            keyboardType = KeyboardType.Number
        )

        Button(
            onClick = {
                if (monthlyInvestment.toDoubleOrNull() == null || years.toIntOrNull() == null || rate.toDoubleOrNull() == null) {
                    android.widget.Toast.makeText(context, "Please enter valid numbers", android.widget.Toast.LENGTH_SHORT).show()
                    return@Button
                }
                onCalculate()
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Calculate")
        }

        // Info card
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(
                    text = "SIP allows you to invest small amounts regularly, benefiting from rupee cost averaging and compounding.",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
fun SWPCalculatorForm(
    params: FinanceParams,
    onParamsChange: (FinanceParams) -> Unit,
    onCalculate: () -> Unit
) {
    val context = LocalContext.current
    var corpus by remember { mutableStateOf(if (params.corpus ?: 0.0 > 0.0) params.corpus.toString() else "") }
    var years by remember { mutableStateOf(if (params.years > 0) params.years.toString() else "") }
    var rate by remember { mutableStateOf(if (params.rate > 0) params.rate.toString() else "") }
    var monthlyWithdrawal by remember { mutableStateOf(if (params.monthlyWithdrawal > 0) params.monthlyWithdrawal.toString() else "") }

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "SWP Calculator",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        LabeledTextField(
            label = "Initial Corpus (₹)",
            text = corpus,
            onTextChange = {
                corpus = it
                onParamsChange(params.copy(corpus = it.toDoubleOrNull() ?: 0.0))
            },
            keyboardType = KeyboardType.Number
        )

        LabeledTextField(
            label = "Withdrawal Period (Years)",
            text = years,
            onTextChange = {
                years = it
                onParamsChange(params.copy(years = it.toIntOrNull() ?: 0))
            },
            keyboardType = KeyboardType.Number
        )

        LabeledTextField(
            label = "Expected Return Rate (%)",
            text = rate,
            onTextChange = {
                rate = it
                onParamsChange(params.copy(rate = it.toDoubleOrNull() ?: 0.0))
            },
            keyboardType = KeyboardType.Number
        )

        LabeledTextField(
            label = "Monthly Withdrawal (₹) - Optional",
            text = monthlyWithdrawal,
            onTextChange = {
                monthlyWithdrawal = it
                onParamsChange(params.copy(monthlyWithdrawal = it.toDoubleOrNull() ?: 0.0))
            },
            keyboardType = KeyboardType.Number
        )

        Button(
            onClick = {
                if (corpus.toDoubleOrNull() == null || years.toIntOrNull() == null || rate.toDoubleOrNull() == null) {
                    android.widget.Toast.makeText(context, "Please enter valid numbers", android.widget.Toast.LENGTH_SHORT).show()
                    return@Button
                }
                onCalculate()
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Calculate")
        }

        // Info card
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(
                    text = "SWP allows you to withdraw fixed amounts regularly from your investment corpus while the remaining amount continues to earn returns.",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
fun EMICalculatorForm(
    params: FinanceParams,
    onParamsChange: (FinanceParams) -> Unit,
    onCalculate: () -> Unit
) {
    val context = LocalContext.current
    var principal by remember { mutableStateOf(if (params.principal > 0) params.principal.toString() else "") }
    var years by remember { mutableStateOf(if (params.years > 0) params.years.toString() else "") }
    var rate by remember { mutableStateOf(if (params.rate > 0) params.rate.toString() else "") }

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "EMI Calculator",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        LabeledTextField(
            label = "Loan Amount (₹)",
            text = principal,
            onTextChange = {
                principal = it
                onParamsChange(params.copy(principal = it.toDoubleOrNull() ?: 0.0))
            },
            keyboardType = KeyboardType.Number
        )

        LabeledTextField(
            label = "Loan Tenure (Years)",
            text = years,
            onTextChange = {
                years = it
                onParamsChange(params.copy(years = it.toIntOrNull() ?: 0))
            },
            keyboardType = KeyboardType.Number
        )

        LabeledTextField(
            label = "Interest Rate (%)",
            text = rate,
            onTextChange = {
                rate = it
                onParamsChange(params.copy(rate = it.toDoubleOrNull() ?: 0.0))
            },
            keyboardType = KeyboardType.Number
        )

        Button(
            onClick = {
                if (principal.toDoubleOrNull() == null || years.toIntOrNull() == null || rate.toDoubleOrNull() == null) {
                    android.widget.Toast.makeText(context, "Please enter valid numbers", android.widget.Toast.LENGTH_SHORT).show()
                    return@Button
                }
                onCalculate()
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Calculate")
        }

        // Info card
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(
                    text = "EMI is the fixed payment amount made by a borrower to a lender at a specified date each calendar month.",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
fun CAGRCalculatorForm(
    params: FinanceParams,
    onParamsChange: (FinanceParams) -> Unit,
    onCalculate: () -> Unit
) {
    val context = LocalContext.current
    var beginningValue by remember { mutableStateOf(if (params.beginningValue > 0) params.beginningValue.toString() else "") }
    var endingValue by remember { mutableStateOf(if (params.endingValue > 0) params.endingValue.toString() else "") }
    var years by remember { mutableStateOf(if (params.years > 0) params.years.toString() else "") }

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "CAGR Calculator",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        LabeledTextField(
            label = "Beginning Value (₹)",
            text = beginningValue,
            onTextChange = {
                beginningValue = it
                onParamsChange(params.copy(beginningValue = it.toDoubleOrNull() ?: 0.0))
            },
            keyboardType = KeyboardType.Number
        )

        LabeledTextField(
            label = "Ending Value (₹)",
            text = endingValue,
            onTextChange = {
                endingValue = it
                onParamsChange(params.copy(endingValue = it.toDoubleOrNull() ?: 0.0))
            },
            keyboardType = KeyboardType.Number
        )

        LabeledTextField(
            label = "Investment Period (Years)",
            text = years,
            onTextChange = {
                years = it
                onParamsChange(params.copy(years = it.toIntOrNull() ?: 0))
            },
            keyboardType = KeyboardType.Number
        )

        Button(
            onClick = {
                if (beginningValue.toDoubleOrNull() == null || endingValue.toDoubleOrNull() == null || years.toIntOrNull() == null) {
                    android.widget.Toast.makeText(context, "Please enter valid numbers", android.widget.Toast.LENGTH_SHORT).show()
                    return@Button
                }
                onCalculate()
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Calculate")
        }

        // Info card
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(
                    text = "CAGR is the rate of return required for an investment to grow from its beginning balance to its ending balance.",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
fun LumpsumCalculatorForm(
    params: FinanceParams,
    onParamsChange: (FinanceParams) -> Unit,
    onCalculate: () -> Unit
) {
    val context = LocalContext.current
    var principal by remember { mutableStateOf(if (params.principal > 0) params.principal.toString() else "") }
    var years by remember { mutableStateOf(if (params.years > 0) params.years.toString() else "") }
    var rate by remember { mutableStateOf(if (params.rate > 0) params.rate.toString() else "") }

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Lumpsum Calculator",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        LabeledTextField(
            label = "Investment Amount (₹)",
            text = principal,
            onTextChange = {
                principal = it
                onParamsChange(params.copy(principal = it.toDoubleOrNull() ?: 0.0))
            },
            keyboardType = KeyboardType.Number
        )

        LabeledTextField(
            label = "Investment Period (Years)",
            text = years,
            onTextChange = {
                years = it
                onParamsChange(params.copy(years = it.toIntOrNull() ?: 0))
            },
            keyboardType = KeyboardType.Number
        )

        LabeledTextField(
            label = "Expected Return Rate (%)",
            text = rate,
            onTextChange = {
                rate = it
                onParamsChange(params.copy(rate = it.toDoubleOrNull() ?: 0.0))
            },
            keyboardType = KeyboardType.Number
        )

        Button(
            onClick = {
                if (principal.toDoubleOrNull() == null || years.toIntOrNull() == null || rate.toDoubleOrNull() == null) {
                    android.widget.Toast.makeText(context, "Please enter valid numbers", android.widget.Toast.LENGTH_SHORT).show()
                    return@Button
                }
                onCalculate()
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Calculate")
        }

        // Info card
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(
                    text = "Lumpsum investment involves investing a large sum of money at one time instead of spreading it out over multiple installments.",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
fun FDCalculatorForm(
    params: FinanceParams,
    onParamsChange: (FinanceParams) -> Unit,
    onCalculate: () -> Unit
) {
    val context = LocalContext.current
    var principal by remember { mutableStateOf(if (params.principal > 0) params.principal.toString() else "") }
    var years by remember { mutableStateOf(if (params.years > 0) params.years.toString() else "") }
    var rate by remember { mutableStateOf(if (params.rate > 0) params.rate.toString() else "") }

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Fixed Deposit Calculator",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        LabeledTextField(
            label = "Deposit Amount (₹)",
            text = principal,
            onTextChange = {
                principal = it
                onParamsChange(params.copy(principal = it.toDoubleOrNull() ?: 0.0))
            },
            keyboardType = KeyboardType.Number
        )

        LabeledTextField(
            label = "Deposit Period (Years)",
            text = years,
            onTextChange = {
                years = it
                onParamsChange(params.copy(years = it.toIntOrNull() ?: 0))
            },
            keyboardType = KeyboardType.Number
        )

        LabeledTextField(
            label = "Interest Rate (%)",
            text = rate,
            onTextChange = {
                rate = it
                onParamsChange(params.copy(rate = it.toDoubleOrNull() ?: 0.0))
            },
            keyboardType = KeyboardType.Number
        )

        Button(
            onClick = {
                if (principal.toDoubleOrNull() == null || years.toIntOrNull() == null || rate.toDoubleOrNull() == null) {
                    android.widget.Toast.makeText(context, "Please enter valid numbers", android.widget.Toast.LENGTH_SHORT).show()
                    return@Button
                }
                onCalculate()
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Calculate")
        }

        // Info card
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(
                    text = "Fixed Deposit is a financial instrument provided by banks which provides investors with a higher rate of interest than a regular savings account.",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
fun RDCalculatorForm(
    params: FinanceParams,
    onParamsChange: (FinanceParams) -> Unit,
    onCalculate: () -> Unit
) {
    val context = LocalContext.current
    var monthlyInvestment by remember { mutableStateOf(if (params.monthlyInvestment > 0) params.monthlyInvestment.toString() else "") }
    var years by remember { mutableStateOf(if (params.years > 0) params.years.toString() else "") }
    var rate by remember { mutableStateOf(if (params.rate > 0) params.rate.toString() else "") }

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Recurring Deposit Calculator",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        LabeledTextField(
            label = "Monthly Investment (₹)",
            text = monthlyInvestment,
            onTextChange = {
                monthlyInvestment = it
                onParamsChange(params.copy(monthlyInvestment = it.toDoubleOrNull() ?: 0.0))
            },
            keyboardType = KeyboardType.Number
        )

        LabeledTextField(
            label = "Investment Period (Years)",
            text = years,
            onTextChange = {
                years = it
                onParamsChange(params.copy(years = it.toIntOrNull() ?: 0))
            },
            keyboardType = KeyboardType.Number
        )

        LabeledTextField(
            label = "Interest Rate (%)",
            text = rate,
            onTextChange = {
                rate = it
                onParamsChange(params.copy(rate = it.toDoubleOrNull() ?: 0.0))
            },
            keyboardType = KeyboardType.Number
        )

        Button(
            onClick = {
                if (monthlyInvestment.toDoubleOrNull() == null || years.toIntOrNull() == null || rate.toDoubleOrNull() == null) {
                    android.widget.Toast.makeText(context, "Please enter valid numbers", android.widget.Toast.LENGTH_SHORT).show()
                    return@Button
                }
                onCalculate()
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Calculate")
        }

        // Info card
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(
                    text = "Recurring Deposit is a special kind of term deposit offered by banks which help people with regular incomes to deposit a fixed amount every month.",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
fun BasicCalculatorForm() {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Basic Calculator",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Text(
                text = "For basic calculations, please use the AI Assistant with natural language queries like:\n\n" +
                      "• What is 5000 + 3000?\n" +
                      "• Calculate 15% of 25000\n" +
                      "• What is 12000 * 5?",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}
