package com.example.nammasantheledger.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.nammasantheledger.data.Customer
import com.example.nammasantheledger.data.LedgerRepository
import com.example.nammasantheledger.data.Transaction
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class TransactionViewModel(private val repository: LedgerRepository) : ViewModel() {

    fun transactionsForCustomer(customerId: Int): StateFlow<List<Transaction>> =
        repository.getTransactionsByCustomer(customerId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun netBalance(customerId: Int): StateFlow<Double> =
        repository.getCustomerBalance(customerId)
            .map { it ?: 0.0 }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    fun addTransaction(customerId: Int, amount: Double, type: String, note: String, onComplete: () -> Unit) {
        viewModelScope.launch {
            repository.insertTransaction(
                Transaction(customerId = customerId, amount = amount, type = type, note = note)
            )
            onComplete()
        }
    }

    suspend fun getCustomerById(id: Int): Customer? = repository.getCustomerById(id)
}

class TransactionViewModelFactory(private val repository: LedgerRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TransactionViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TransactionViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
