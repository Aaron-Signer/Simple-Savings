package com.example.simplesavings.composable.transactions

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.forEach
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.window.PopupProperties
import com.example.simplesavings.model.transaction.Transaction
import java.time.Instant
import com.example.simplesavings.util.db.getTransactionSha256Uid


@SuppressLint("UnrememberedMutableState")
@Composable
fun CreateTransactionForm(
    modifier: Modifier = Modifier,
    db: AppDatabase,
    onDismiss: () -> Unit,
    currentMonthString: String,
    currentYearString: String
) {
    val scope = rememberCoroutineScope()
    var businessName by remember {mutableStateOf("")}
    var debit by remember { mutableStateOf("")}
    var credit by remember {mutableStateOf("")}

    var mExpanded by remember { mutableStateOf(false) }

    // Create a string value to store the selected city
    var selectedCategory by remember { mutableStateOf(Category(-1, -1, "", categoryYear = "", categoryMonth = "")) }
    var selectedCategoryName by remember { mutableStateOf("") }

    var mTextFieldSize by remember { mutableStateOf(Size.Zero)}

    // Up Icon when expanded and down icon when collapsed
    val icon = if (mExpanded)
        Icons.Filled.KeyboardArrowUp
    else
        Icons.Filled.KeyboardArrowDown

    val categoryList by db.categoryDao().getCategoriesForMonthAndYear(currentMonthString, currentYearString).collectAsState(initial = emptyList())
    var filteredCategoryList = categoryList.map { it.copy() }
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
                .padding(10.dp),
            colors = CardDefaults.elevatedCardColors(
                containerColor = Color(0xFF272727), // Set your background color here
                contentColor = Color.White         // Optional: Set default text color
            ),
        ) {
            Text(
                text = "Transaction Form",
                Modifier.padding(10.dp)
            )
            TextField(
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFF272727),
                    unfocusedContainerColor = Color(0xFF272727),
                    disabledContainerColor = Color(0xFF272727),
                ),
                value = businessName,
                onValueChange = { businessName = it },
                label = { Text("Business Name")},
                modifier = Modifier
                    .fillMaxWidth(.75F)
                    .padding(10.dp)
            )

            TextField (
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFF272727),
                    unfocusedContainerColor = Color(0xFF272727),
                    disabledContainerColor = Color(0xFF272727),
                ),
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
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFF272727),
                    unfocusedContainerColor = Color(0xFF272727),
                    disabledContainerColor = Color(0xFF272727),
                ),
            )

            Text(
                text = "Category: ${selectedCategory.name}",
                Modifier.padding(10.dp)
            )

            // Replace your "Select Category" Box/Column with this:
            Box(modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth(.75F)) {
                Column {
                    TextField(
                        value = selectedCategoryName,
                        onValueChange = {
                            selectedCategoryName = it
                            mExpanded = true // Show dropdown as user types
                            filteredCategoryList = categoryList.filter { category ->
                                category.name.contains(selectedCategoryName)
                            }
                        },
                        label = { Text("Search Category") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFF272727),
                            unfocusedContainerColor = Color(0xFF272727)
                        ),
                        trailingIcon = {
                            IconButton(onClick = { mExpanded = !mExpanded }) {
                                Icon(icon, contentDescription = "Expand")
                            }
                        }
                    )

                    DropdownMenu(
                        expanded = mExpanded && categoryList.isNotEmpty(),
                        onDismissRequest = { mExpanded = false },
                        // Match the width of the TextField
                        modifier = Modifier.fillMaxWidth(.75F),
                        properties = PopupProperties(focusable = false),
                    ) {
                        filteredCategoryList.forEach { category ->
                            DropdownMenuItem(
                                text = { Text(text = category.name) },
                                onClick = {
                                    selectedCategory = category
                                    selectedCategoryName = selectedCategory.name
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
                            selectedCategory = Category(-1, -1, "", categoryYear = "", categoryMonth = "")
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
