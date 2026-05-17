package com.example.nammasantheledger.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nammasantheledger.data.Customer
import com.example.nammasantheledger.ui.theme.ErrorTomato
import com.example.nammasantheledger.ui.theme.PaidGreen
import com.example.nammasantheledger.viewmodel.CustomerViewModel
import com.example.nammasantheledger.viewmodel.TransactionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionScreen(
    transactionViewModel: TransactionViewModel,
    customerViewModel: CustomerViewModel,
    onBack: () -> Unit
) {
    var selectedCustomer by remember { mutableStateOf<Customer?>(null) }
    var amountText by remember { mutableStateOf("") }
    var transactionType by remember { mutableStateOf("CREDIT") } // CREDIT or PAYMENT
    var note by remember { mutableStateOf("") }
    var step by remember { mutableStateOf(1) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (step == 1) "Select Customer" else "Enter Amount") },
                navigationIcon = {
                    IconButton(onClick = {
                        if (step == 2) step = 1 else onBack()
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            if (step == 1) {
                CustomerSelectionStep(
                    viewModel = customerViewModel,
                    onCustomerSelected = {
                        selectedCustomer = it
                        step = 2
                    }
                )
            } else {
                TransactionDetailsStep(
                    customerName = selectedCustomer?.name ?: "",
                    amountText = amountText,
                    transactionType = transactionType,
                    note = note,
                    onAmountChange = { amountText = it },
                    onTypeChange = { transactionType = it },
                    onNoteChange = { note = it },
                    onSave = {
                        val amount = amountText.toDoubleOrNull() ?: 0.0
                        if (amount > 0 && selectedCustomer != null) {
                            transactionViewModel.addTransaction(
                                customerId = selectedCustomer!!.id,
                                amount = amount,
                                type = transactionType,
                                note = note,
                                onComplete = onBack
                            )
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun CustomerSelectionStep(
    viewModel: CustomerViewModel,
    onCustomerSelected: (Customer) -> Unit
) {
    val customers by viewModel.filteredCustomers.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.updateSearchQuery(it) },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Search customer...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(customers) { customer ->
                val displayedPhone = customer.phone.let {
                    if (it.startsWith("91") && it.length == 12) it.substring(2) else it
                }
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onCustomerSelected(customer) },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = customer.name,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "+91 $displayedPhone",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TransactionDetailsStep(
    customerName: String,
    amountText: String,
    transactionType: String,
    note: String,
    onAmountChange: (String) -> Unit,
    onTypeChange: (String) -> Unit,
    onNoteChange: (String) -> Unit,
    onSave: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Transaction for $customerName", style = MaterialTheme.typography.titleMedium, color = Color.Gray)
        
        Spacer(modifier = Modifier.height(16.dp))

        // Amount Display
        Text(
            text = "₹${if (amountText.isEmpty()) "0" else amountText}",
            style = MaterialTheme.typography.displayLarge,
            fontWeight = FontWeight.Bold,
            color = if (transactionType == "CREDIT") ErrorTomato else PaidGreen
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Type Toggle
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color.LightGray.copy(alpha = 0.3f))
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (transactionType == "CREDIT") ErrorTomato else Color.Transparent)
                    .clickable { onTypeChange("CREDIT") }
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("Udari (Credit)", color = if (transactionType == "CREDIT") Color.White else Color.Black)
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (transactionType == "PAYMENT") PaidGreen else Color.Transparent)
                    .clickable { onTypeChange("PAYMENT") }
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("Payment", color = if (transactionType == "PAYMENT") Color.White else Color.Black)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = note,
            onValueChange = onNoteChange,
            label = { Text("Note (optional)") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.weight(1f))

        // Custom Keypad
        CustomKeypad(
            onKeyPress = { key ->
                if (key == "DEL") {
                    if (amountText.isNotEmpty()) onAmountChange(amountText.dropLast(1))
                } else if (key == ".") {
                    if (!amountText.contains(".")) onAmountChange(amountText + key)
                } else {
                    onAmountChange(amountText + key)
                }
            },
            onDone = onSave
        )
    }
}

@Composable
fun CustomKeypad(onKeyPress: (String) -> Unit, onDone: () -> Unit) {
    val keys = listOf(
        listOf("1", "2", "3"),
        listOf("4", "5", "6"),
        listOf("7", "8", "9"),
        listOf(".", "0", "DEL")
    )

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        keys.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                row.forEach { key ->
                    KeyButton(
                        text = key,
                        modifier = Modifier.weight(1f),
                        onClick = { onKeyPress(key) }
                    )
                }
            }
        }
        Button(
            onClick = onDone,
            modifier = Modifier.fillMaxWidth().height(64.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Icon(Icons.Default.Check, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("DONE", style = MaterialTheme.typography.titleLarge)
        }
    }
}

@Composable
fun KeyButton(text: String, modifier: Modifier, onClick: () -> Unit) {
    FilledTonalButton(
        onClick = onClick,
        modifier = modifier.height(64.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        if (text == "DEL") {
            Icon(Icons.Default.Close, contentDescription = null)
        } else {
            Text(text = text, style = MaterialTheme.typography.headlineMedium)
        }
    }
}
