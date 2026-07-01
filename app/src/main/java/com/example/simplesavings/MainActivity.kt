package com.example.simplesavings

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import com.example.simplesavings.composable.budget.BudgetScreen
import com.example.simplesavings.composable.dataVisualization.SampleChart2
import com.example.simplesavings.composable.income.IncomeScreen
import com.example.simplesavings.composable.navigation.NavigationRow
import com.example.simplesavings.composable.transactions.TransactionsView
import com.example.simplesavings.config.database.AppDatabase
import com.example.simplesavings.config.database.MIGRATION_24_25
import com.example.simplesavings.enums.Navigation
import com.example.simplesavings.ui.theme.SimpleSavingsTheme
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SimpleSavingsTheme {
                Scaffold(
                    containerColor = Color(0xFF272727),
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFF272727))
                ) { innerPadding ->
                    val navController= rememberNavController()

                    val context = LocalContext.current
//
                    val db = remember {
                        Room.databaseBuilder(
                            context,
                            AppDatabase::class.java, "group"
                        )
                            .addMigrations(MIGRATION_24_25)
                            .build()
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .background(Color(0xFF272727))
                    ) {
                        val yearFormatter = DateTimeFormatter.ofPattern("YYYY", Locale.getDefault())
                        var currentMonthAndYear by remember {mutableStateOf(Instant.now())}
                        var currentYearString = currentMonthAndYear
                            .atZone(ZoneId.systemDefault())
                            .format(yearFormatter)

                        val monthFormatter = DateTimeFormatter.ofPattern("MMMM", Locale.getDefault())
                        var currentMonthString = currentMonthAndYear
                            .atZone(ZoneId.systemDefault())
                            .format(monthFormatter)


                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            IconButton(
                                onClick = {
                                    currentMonthAndYear = currentMonthAndYear
                                        .atZone(ZoneId.systemDefault()) // Convert to ZonedDateTime
                                        .minusMonths(1)                // Subtract one month
                                        .toInstant()

                                    currentMonthString = currentMonthAndYear
                                        .atZone(ZoneId.systemDefault())
                                        .format(monthFormatter)

                                    currentYearString = currentMonthAndYear
                                        .atZone(ZoneId.systemDefault())
                                        .format(yearFormatter)
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Backward",
                                    tint = Color.White
                                )
                            }
                            Text(
                                text = "${currentMonthString} ${currentYearString}",
                                color = Color.White
                            )
                            IconButton(
                                onClick = {
                                    currentMonthAndYear = currentMonthAndYear
                                        .atZone(ZoneId.systemDefault())
                                        .plusMonths(1)
                                        .toInstant()

                                    currentMonthString = currentMonthAndYear
                                        .atZone(ZoneId.systemDefault())
                                        .format(monthFormatter)

                                    currentYearString = currentMonthAndYear
                                        .atZone(ZoneId.systemDefault())
                                        .format(yearFormatter)
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = "Forward",
                                    tint = Color.White
                                )
                            }
                        }

                        NavHost(
                            navController = navController,
                            startDestination = Navigation.Budget.name,
                            modifier = Modifier.weight(1f)
                        ) {
                            composable(route = Navigation.TransactionSummary.name) {
                                SampleChart2 (
                                    modifier = Modifier.fillMaxSize(),
                                    db = db,
                                    currentMonthString,
                                    currentYearString
                                )
                            }

                            composable(route = Navigation.Income.name) {
                                IncomeScreen (
                                    db = db
                                )
                            }

                            composable(route = Navigation.Budget.name) {
                                BudgetScreen (
                                    db = db,
                                    currentMonthAndYear = currentMonthAndYear,
                                    monthFormatter = monthFormatter,
                                    yearFormatter = yearFormatter
                                )
                            }

                            composable(route = Navigation.Transactions.name) {
                                TransactionsView (
                                    modifier = Modifier.fillMaxSize(),
                                    db = db,
                                    currentMonthString,
                                    currentYearString
                                )
                            }
                        }
                        
                        NavigationRow(
                            navController = navController
                        )
                    }
                }
            }
        }
    }
}

