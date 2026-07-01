package com.example.simplesavings.composable.transactions


import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import androidx.compose.ui.zIndex
import com.example.simplesavings.config.database.AppDatabase
import com.example.simplesavings.model.category.Category
import com.example.simplesavings.model.transaction.Transaction
import com.example.simplesavings.util.db.getTransactionSha256Uid
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale


@SuppressLint("UnrememberedMutableState")
@Composable
fun CreateTransactionForm(
    modifier: Modifier = Modifier,
    db: AppDatabase,
    onDismiss: () -> Unit,
    currentMonthString: String,
    currentYearString: String,
    selectedTransaction: Transaction?
) {
    val scope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current

    // UI Toggle Vars
    var mExpanded by remember { mutableStateOf(false) }
    var showDateRangePicker by remember { mutableStateOf(false) }

    // Selected Category Vars
    var selectedCategory by remember { mutableStateOf(Category(-1, -1, "", categoryYear = "", categoryMonth = "")) }
    var selectedCategoryName by remember { mutableStateOf("") }

    val categoryList by db.categoryDao()
        .getCategoriesForMonthAndYear(currentMonthString, currentYearString)
        .collectAsState(initial = emptyList())

    val filteredCategoryList = remember(categoryList, selectedCategoryName) {
        categoryList.filter { it.name.contains(selectedCategoryName, ignoreCase = true) }
    }

    // Transaction fields
    var businessName by remember {mutableStateOf("")}
    var debit by remember { mutableStateOf("")}
    var credit by remember {mutableStateOf("")}
    var dateTime by remember { mutableStateOf(Instant.now()) }

    if (selectedTransaction != null) {
        businessName = selectedTransaction.businessName
        debit = selectedTransaction.debit.toString()
        credit = selectedTransaction.credit.toString()
        dateTime = selectedTransaction.dateTime
        selectedCategoryName = selectedTransaction.categoryName

        LaunchedEffect(categoryList) {
            if (categoryList.isNotEmpty()) {
                selectedCategory = categoryList.find { it.uid == selectedTransaction.categoryUid }
                    ?: Category(-1, -1, "", categoryYear = "", categoryMonth = "")
            }
        }
    }

    // Up Icon when expanded and down icon when collapsed
    val icon = if (mExpanded)
        Icons.Filled.KeyboardArrowUp
    else
        Icons.Filled.KeyboardArrowDown

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
            val formatter = DateTimeFormatter.ofPattern("dd")
            val currentDay = dateTime.atZone(ZoneId.systemDefault()).format(formatter)

            Text(
                modifier = modifier.clickable {
                    showDateRangePicker = true
                },
                text = "Selected Date: ${currentMonthString} ${currentDay}, ${currentYearString}"
            )

            if (showDateRangePicker) {
                MyDatePickerDialog(
                    onDateSelected = { milliseconds ->
                        milliseconds?.let {
                            dateTime = Instant.ofEpochMilli(milliseconds)
                        }

                    },
                    onDismiss = {
                        showDateRangePicker = false
                    }
                )
            }

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
                        scope.launch {
                            if (selectedTransaction != null) {
                                db.transactionDao().update(selectedTransaction)
                            } else {
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
                                selectedCategoryName = ""
                                selectedCategory = Category(-1, -1, "", categoryYear = "", categoryMonth = "")
                            }
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyDatePickerDialog(
    onDateSelected: (Long?) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState()

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                onDateSelected(datePickerState.selectedDateMillis)
                onDismiss()
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}

fun getSelectedCategory(
    categoryList: List<Category>,
    categoryUid: Int
):  Category {
    return categoryList.filter{
        it.uid  == categoryUid
    }[0]
}