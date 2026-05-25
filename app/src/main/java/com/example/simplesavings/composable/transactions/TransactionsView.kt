package com.example.simplesavings.composable.transactions

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex

import com.example.simplesavings.config.database.AppDatabase

@SuppressLint("UnrememberedMutableState")
@Composable
fun TransactionsView (
    modifier: Modifier = Modifier,
    db: AppDatabase
) {
    val transactionList by db.transactionDao().getAll().collectAsState(initial = emptyList())

    var showCreateTransactionForm by remember { mutableStateOf( false) }

    if (showCreateTransactionForm) {
        CreateTransactionForm (
            Modifier,
            db = db,
            { showCreateTransactionForm = false })
    }

//    val transactionList = mutableListOf<Transaction>()
//
//    for (i in 1..40) {
//        transactionList.add(
//            Transaction(
//                uid = i,
//                categoryUid = 1,
//                debit = 10.0 * i,
//                businessName = "Business $i"
//            )
//        )
//    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp)
                .verticalScroll(rememberScrollState())
        ) {
            for (transaction in transactionList) {
                ElevatedCard(
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 6.dp
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 5.dp, vertical = 5.dp)
                ) {

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp)
                    ) {
                        Text(
                            text = "Business: ${transaction.businessName}"
                        )

                        Text(
                            text = "Category: ${transaction.categoryUid}"
                        )

                        if (transaction.debit != 0.0) {
                            Text(
                                text = "+${transaction.debit}",
                                color = Color.Green
                            )
                        } else if (transaction.credit != 0.0) {
                            Text(
                                text = "-${transaction.credit}",
                                color = Color.Red
                            )
                        }
                    }
                }
            }
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .zIndex(100F)
                .padding(16.dp),
            contentAlignment = Alignment.BottomEnd,
        ) {
            Button(
                onClick = {
                    showCreateTransactionForm = true
                }
            ) {
                Text(
                    text = "Add Transaction"
                )
            }
        }
    }
}
