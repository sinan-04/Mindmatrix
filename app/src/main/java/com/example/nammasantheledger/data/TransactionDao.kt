package com.example.nammasantheledger.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(transaction: Transaction)

    @Query("SELECT * FROM transactions WHERE customerId = :customerId ORDER BY date DESC")
    fun getTransactionsByCustomer(customerId: Int): Flow<List<Transaction>>

    @Query("SELECT * FROM transactions WHERE date >= :startOfDay ORDER BY date DESC")
    fun getTodayTransactions(startOfDay: Long): Flow<List<Transaction>>

    @Query("SELECT * FROM transactions ORDER BY date DESC LIMIT 5")
    fun getRecentTransactions(): Flow<List<Transaction>>

    @Query("SELECT SUM(CASE WHEN type = 'CREDIT' THEN amount ELSE -amount END) FROM transactions")
    fun getTotalOutstanding(): Flow<Double?>

    @Query("SELECT SUM(amount) FROM transactions WHERE type = 'CREDIT' AND date >= :startOfDay")
    fun getTodaySales(startOfDay: Long): Flow<Double?>
    
    @Query("SELECT SUM(CASE WHEN type = 'CREDIT' THEN amount ELSE -amount END) FROM transactions WHERE customerId = :customerId")
    fun getCustomerBalance(customerId: Int): Flow<Double?>
}
