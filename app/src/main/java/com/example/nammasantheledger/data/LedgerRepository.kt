package com.example.nammasantheledger.data

import kotlinx.coroutines.flow.Flow
import java.util.*

class LedgerRepository(private val customerDao: CustomerDao, private val transactionDao: TransactionDao) {

    val allCustomers: Flow<List<Customer>> = customerDao.getAllCustomers()

    fun searchCustomers(query: String): Flow<List<Customer>> = customerDao.searchCustomers(query)

    suspend fun insertCustomer(customer: Customer) = customerDao.insert(customer)

    suspend fun updateCustomer(customer: Customer) = customerDao.update(customer)

    suspend fun getCustomerById(id: Int): Customer? = customerDao.getCustomerById(id)

    suspend fun getCustomerByName(name: String): Customer? = customerDao.getCustomerByName(name)

    fun getTransactionsByCustomer(customerId: Int): Flow<List<Transaction>> =
        transactionDao.getTransactionsByCustomer(customerId)

    fun getRecentTransactions(): Flow<List<Transaction>> = transactionDao.getRecentTransactions()

    fun getTotalOutstanding(): Flow<Double?> = transactionDao.getTotalOutstanding()

    fun getTodaySales(): Flow<Double?> {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return transactionDao.getTodaySales(calendar.timeInMillis)
    }

    fun getCustomerBalance(customerId: Int): Flow<Double?> = transactionDao.getCustomerBalance(customerId)

    suspend fun insertTransaction(transaction: Transaction) = transactionDao.insert(transaction)
}
