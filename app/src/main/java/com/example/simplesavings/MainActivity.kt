package com.example.simplesavings

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.simplesavings.composable.budget.BudgetScreen
import com.example.simplesavings.composable.transactions.TransactionsView
import com.example.simplesavings.config.database.AppDatabase
import com.example.simplesavings.enums.Navigation
import com.example.simplesavings.ui.theme.SimpleSavingsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SimpleSavingsTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val navController= rememberNavController()

                    val context = LocalContext.current
//
                    val db = remember {
                        Room.databaseBuilder(
                            context,
                            AppDatabase::class.java, "group"
                        )
                            .fallbackToDestructiveMigration(true)
                            .build()
                    }

                    Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Button (
                                onClick = {
                                    navController.navigate(Navigation.Budget.name)
                                }
                            ) {
                                Text (
                                    text = "Budget"
                                )
                            }
                            Button (
                                onClick = {
                                    navController.navigate(Navigation.Transactions.name)
                                }
                            ) {
                                Text (
                                    text = "Transactions"
                                )
                            }
                        }

                        NavHost(
                            navController = navController,
                            startDestination = Navigation.Budget.name,
                            modifier = Modifier.weight(1f)
                        ) {
//                        composable(route = Navigation.Analytics.name) {
//                            val context = LocalContext.current
//                            SelectOptionScreen(
//                                subtotal = uiState.price,
//                                options = DataSource.flavors.map { id -> context.resources.getString(id) }
//                            )
//                        }
                            composable(route = Navigation.Budget.name) {
                                BudgetScreen (
                                    db = db
                                )
                            }

                            composable(route = Navigation.Transactions.name) {
                                TransactionsView (
                                    modifier = Modifier.fillMaxSize(),
                                    db = db
                                )
                            }
                        }
                    }
//                    val context = LocalContext.current
//
//                    val db = remember {
//                        Room.databaseBuilder(
//                            context,
//                            AppDatabase::class.java, "group"
//                        )
//                            .fallbackToDestructiveMigration(true)
//                            .build()
//                    }
//
//                    var showTransaction by remember { mutableStateOf( false) }
//
//                    Row(
//                        modifier = Modifier.fillMaxSize().padding(top = 50.dp, end = 20.dp, start = 20.dp),
//                        horizontalArrangement = Arrangement.SpaceBetween
//                    ) {
//                        Button (
//                            onClick = {
//                                showTransaction = false
//                            }
//                        ) {
//                            Text (
//                                text = "Budget"
//                            )
//                        }
//                        Button (
//                            onClick = {
//                                showTransaction = true
//                            }
//                        ) {
//                            Text (
//                                text = "Transactions"
//                            )
//                        }
//                    }
//
//                    if (showTransaction) {
//                        TransactionsView(
//                            modifier = Modifier,
//                            db
//                        )
//                    }
//                    else if (!showTransaction) {
//                        BudgetScreen (
//                            modifier = Modifier.padding(innerPadding),
//                            db
//                        )
//                    }
                }
            }
        }
    }
}

