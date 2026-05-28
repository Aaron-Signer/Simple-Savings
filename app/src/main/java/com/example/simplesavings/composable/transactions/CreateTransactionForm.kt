package com.example.simplesavings.composable.transactions

import android.annotation.SuppressLint
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.compose.ui.geometry.Size


import com.example.simplesavings.config.database.AppDatabase
import com.example.simplesavings.model.category.Category
import kotlinx.coroutines.launch

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.KeyboardType
import com.example.simplesavings.model.transaction.Transaction
import java.time.Instant
import com.example.simplesavings.util.db.getTransactionSha256Uid


@SuppressLint("UnrememberedMutableState")
@Composable
fun CreateTransactionForm(
    modifier: Modifier = Modifier,
    db: AppDatabase,
    onDismiss: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var businessName by remember {mutableStateOf("")}
    var debit by remember { mutableStateOf("")}
    var credit by remember {mutableStateOf("")}

    var mExpanded by remember { mutableStateOf(false) }

    // Create a string value to store the selected city
    var selectedCategory by remember { mutableStateOf(Category(-1, -1, "")) }

    var mTextFieldSize by remember { mutableStateOf(Size.Zero)}

    // Up Icon when expanded and down icon when collapsed
    val icon = if (mExpanded)
        Icons.Filled.KeyboardArrowUp
    else
        Icons.Filled.KeyboardArrowDown

    val categoryList by db.categoryDao().getAll().collectAsState(initial = emptyList())
    val focusManager = LocalFocusManager.current // 1. Get the focus manager

    Box(
        modifier = modifier
            .fillMaxSize()
            .zIndex(100F)
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    focusManager.clearFocus() // 3. Clear focus to hide keyboard
                })
            },
        contentAlignment = Alignment.TopCenter,

    ) {
        ElevatedCard(
            elevation = CardDefaults.cardElevation(
                defaultElevation = 6.dp
            ),
            modifier = Modifier
                .padding(10.dp)
                .border(1.dp, Color.White)
        ) {
            Text(
                text = "Transaction Form",
                Modifier.padding(10.dp)
            )
            TextField(
                value = businessName,
                onValueChange = { businessName = it },
                label = { Text("Business Name")},
                modifier = Modifier
                    .fillMaxWidth(.75F)
                    .padding(10.dp)
            )

            TextField (
                value = debit,
                onValueChange = { debit = it },
                label = { Text("Debit")},
                modifier = Modifier
                    .fillMaxWidth(.75F)
                    .padding(10.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            )

            TextField (
                value = credit,
                onValueChange = { credit = it },
                label = { Text("Credit")},
                modifier = Modifier
                    .fillMaxWidth(.75F)
                    .padding(10.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            )

            Text(
                text = "Category: ${selectedCategory.name}",
                Modifier.padding(10.dp)
            )

            Box(
                modifier = Modifier
                    .padding(10.dp)
            ) {
                Column() {

                    Button(
                        onClick = {
                            mExpanded = !mExpanded
                        }
                    ) {
                        Text(
                            text = "Select Category"
                        )
                    }
                    DropdownMenu(
                        expanded = mExpanded,
                        onDismissRequest = { mExpanded = false },
                    ) {
                        categoryList.forEach { label ->
                            DropdownMenuItem(
                                text = { Text(text = label.name) },
                                onClick = {
                                    selectedCategory = label
                                    mExpanded = false
                                }
                            )
                        }
                    }
                }
            }
            Row (
                modifier = Modifier.fillMaxWidth(.75F),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    enabled = businessName != "" && selectedCategory.uid != -1 && (credit != "" || debit != ""),
                    onClick = {
                        val dateTime = Instant.now()

                        scope.launch {
                            db.transactionDao().insert(
                                Transaction(
                                    uid = getTransactionSha256Uid(
                                        businessName,
                                        credit.toDouble(),
                                        dateTime
                                    ),
                                    categoryUid = selectedCategory.uid,
                                    dateTime = dateTime,
                                    debit = if (debit == "") 0.0 else debit.toDouble(),
                                    credit = if (credit == "") 0.0 else credit.toDouble(),
                                    businessName = businessName
                                )
                            )

                            businessName = ""
                            credit = ""
                            debit = ""
                            selectedCategory = Category(-1, -1, "")
                        }
                    },
//                enabled = categoryName.value != "" && selectedGroup.uid != -1
                ) {
                    Text(
                        text = "Save Transaction"
                    )
                }
                Button(
                    onClick = {
                        onDismiss()
                    }
                ) {
                    Text(
                        text = "Close"
                    )
                }
            }
        }
    }
}
