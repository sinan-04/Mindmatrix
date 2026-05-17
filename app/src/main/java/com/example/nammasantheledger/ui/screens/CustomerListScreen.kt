package com.example.nammasantheledger.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nammasantheledger.data.Customer
import com.example.nammasantheledger.ui.components.BottomNavigationBar
import com.example.nammasantheledger.ui.theme.ErrorTomato
import com.example.nammasantheledger.ui.theme.PaidGreen
import com.example.nammasantheledger.viewmodel.CustomerViewModel
import java.net.URLEncoder

@Composable
fun CustomerListScreen(
    viewModel: CustomerViewModel,
    onCustomerClick: (Int) -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToCustomers: () -> Unit
) {
    val context = LocalContext.current
    val customers by viewModel.filteredCustomers.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    
    var showEditDialog by remember { mutableStateOf(false) }
    var customerToEdit by remember { mutableStateOf<Customer?>(null) }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                currentRoute = "customer_list",
                onNavigateToHome = onNavigateToHome,
                onNavigateToCustomers = onNavigateToCustomers
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text(
                text = "My Customers",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.updateSearchQuery(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                placeholder = { Text("Search customers...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = Color.LightGray
                )
            )

            if (customers.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("👥", fontSize = 48.sp)
                        Text("No customers found", color = Color.Gray)
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(customers) { customer ->
                        val balance by viewModel.getCustomerBalance(customer.id).collectAsState(initial = 0.0)
                        CustomerCard(
                            customer = customer,
                            balance = balance,
                            onClick = { onCustomerClick(customer.id) },
                            onWhatsAppClick = {
                                sendWhatsAppReminder(context, customer.name, customer.phone, balance)
                            },
                            onSMSClick = {
                                sendSMSReminder(context, customer.name, customer.phone, balance)
                            },
                            onEditClick = {
                                customerToEdit = customer
                                showEditDialog = true
                            }
                        )
                    }
                }
            }
        }
        
        if (showEditDialog && customerToEdit != null) {
            EditCustomerDialog(
                customer = customerToEdit!!,
                onDismiss = { 
                    showEditDialog = false
                    customerToEdit = null
                },
                onConfirm = { updatedCustomer ->
                    viewModel.updateCustomer(updatedCustomer) { success ->
                        if (success) {
                            showEditDialog = false
                            customerToEdit = null
                            Toast.makeText(context, "Customer updated", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Name already exists", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            )
        }
    }
}

@Composable
fun EditCustomerDialog(
    customer: Customer,
    onDismiss: () -> Unit,
    onConfirm: (Customer) -> Unit
) {
    var name by remember { mutableStateOf(customer.name) }
    var phone by remember { 
        mutableStateOf(cleanTo10Digits(customer.phone)) 
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Customer") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Phone Number") },
                    modifier = Modifier.fillMaxWidth(),
                    prefix = { Text("+91 ") }
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                val cleanedPhone = cleanTo10Digits(phone)
                onConfirm(customer.copy(name = name, phone = cleanedPhone))
            }) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun CustomerCard(
    customer: Customer,
    balance: Double,
    onClick: () -> Unit,
    onWhatsAppClick: () -> Unit,
    onSMSClick: () -> Unit,
    onEditClick: () -> Unit
) {
    val displayedPhone = cleanTo10Digits(customer.phone)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                if (balance > 0) {
                    IconButton(
                        onClick = onWhatsAppClick,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "WhatsApp Reminder",
                            tint = Color(0xFF25D366)
                        )
                    }
                    IconButton(
                        onClick = onSMSClick,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = "SMS Reminder",
                            tint = Color(0xFF007AFF)
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                }
                
                IconButton(
                    onClick = onEditClick,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Customer",
                        tint = Color.Gray,
                        modifier = Modifier.size(20.dp)
                    )
                }
                
                Spacer(modifier = Modifier.width(8.dp))

                Column {
                    Text(
                        text = customer.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "+91 $displayedPhone",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = if (balance > 0) "DUE" else "CLEARED",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (balance > 0) ErrorTomato else PaidGreen,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "₹${String.format("%.2f", balance)}",
                    style = MaterialTheme.typography.titleLarge,
                    color = if (balance > 0) ErrorTomato else PaidGreen,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

private fun cleanTo10Digits(phone: String): String {
    val digits = phone.filter { it.isDigit() }
    return if (digits.length >= 12 && digits.startsWith("91")) {
        digits.substring(2, 12)
    } else if (digits.length > 10) {
        digits.takeLast(10)
    } else {
        digits
    }
}

private fun sendWhatsAppReminder(context: Context, name: String, phone: String, amount: Double) {
    val message = "Hello $name, you have a pending due of Rs. ${String.format("%.2f", amount)} at Namma Santhe Ledger. Please clear when convenient. Thank you - From Sinan store"
    val encodedMessage = try { URLEncoder.encode(message, "UTF-8") } catch (e: Exception) { message }
    val basePhone = cleanTo10Digits(phone)
    
    // Use 91 prefix (international format without + is preferred by WA API)
    val url = "https://api.whatsapp.com/send?phone=91$basePhone&text=$encodedMessage"
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))

    try {
        val whatsappIntent = Intent(intent)
        whatsappIntent.setPackage("com.whatsapp")
        context.startActivity(whatsappIntent)
    } catch (e: Exception) {
        try {
            context.startActivity(intent)
        } catch (e2: Exception) {
            Toast.makeText(context, "Could not open WhatsApp or Browser", Toast.LENGTH_SHORT).show()
        }
    }
}

private fun sendSMSReminder(context: Context, name: String, phone: String, amount: Double) {
    val message = "Hello $name, you have a pending due of Rs. ${String.format("%.2f", amount)} at Namma Santhe Ledger. Please clear when convenient. Thank you - From Sinan store"
    val basePhone = cleanTo10Digits(phone)
    
    // Standard SMS URI with +91 prefix
    val uri = Uri.parse("sms:+91$basePhone?body=${Uri.encode(message)}")
    val intent = Intent(Intent.ACTION_VIEW, uri)
    
    // Backup extras
    intent.putExtra("sms_body", message)
    intent.putExtra("body", message)
    
    try {
        Toast.makeText(context, "Opening SMS app... Please tap Send", Toast.LENGTH_SHORT).show()
        context.startActivity(intent)
    } catch (e: Exception) {
        try {
            val fallbackIntent = Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:+91$basePhone"))
            fallbackIntent.putExtra("sms_body", message)
            context.startActivity(fallbackIntent)
        } catch (e2: Exception) {
            Toast.makeText(context, "Could not open SMS app", Toast.LENGTH_SHORT).show()
        }
    }
}
