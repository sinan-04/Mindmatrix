package com.example.nammasantheledger

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.nammasantheledger.data.AppDatabase
import com.example.nammasantheledger.data.LedgerRepository
import com.example.nammasantheledger.ui.navigation.NavGraph
import com.example.nammasantheledger.ui.theme.NammaSantheLedgerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val database = AppDatabase.getDatabase(this)
        val repository = LedgerRepository(database.customerDao(), database.transactionDao())
        
        setContent {
            NammaSantheLedgerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavGraph(repository)
                }
            }
        }
    }
}
