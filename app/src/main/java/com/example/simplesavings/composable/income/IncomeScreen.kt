package com.example.simplesavings.composable.income

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.simplesavings.config.database.AppDatabase
import com.example.simplesavings.model.income.Income

@Composable
fun IncomeScreen(
    db: AppDatabase
) {
    val incomeList = remember {
        mutableStateListOf(
            Income(
                uid = 1,
                name = "paycheck1",
                amount = 3200.0,
                year = "2026",
                month = "June"
            )
        )
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp)
        ) {
            items(
                items = incomeList,
                key = { it.uid }
            ) { income ->
                TransactionCard(
                    income = income
                )
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .zIndex(100F)
                .padding(16.dp),
        ) {
            Row {
                Button(
                    onClick = {

                    }
                ) {
                    Text(
                        text = "Add Income"
                    )
                }
            }
        }
    }
}

@Composable
fun TransactionCard(
    income: Income
) {
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
                text = "Pay Name",
                fontWeight = FontWeight.Bold
            )
            Text(
                text = income.name,
                modifier = Modifier.padding(bottom = 5.dp)
            )

            Text(
                text = "Amount",
                fontWeight = FontWeight.Bold
            )
            Text(
                text = income.amount.toString(),
                modifier = Modifier.padding(bottom = 5.dp)
            )
        }
    }
}