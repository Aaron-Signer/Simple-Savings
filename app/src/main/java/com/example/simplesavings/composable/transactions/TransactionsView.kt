package com.example.simplesavings.composable.transactions

import android.annotation.SuppressLint
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex

import com.example.simplesavings.config.database.AppDatabase
import com.example.simplesavings.model.category.Category
import com.example.simplesavings.model.transaction.Transaction
import com.example.simplesavings.util.db.getTransactionSha256Uid
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.apache.commons.csv.CSVFormat
import java.io.InputStream
import java.security.MessageDigest
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

@Composable
fun TransactionsView (
    modifier: Modifier = Modifier,
    db: AppDatabase
) {
    val transactionListFlow = remember(db) { db.transactionDao().getTransactionsWithNames() }
    val categoryListFlow = remember(db) { db.categoryDao().getAll() }

    val transactionList by transactionListFlow.collectAsState(initial = emptyList())
    val categoryList by categoryListFlow.collectAsState(initial = emptyList())

    val scope = rememberCoroutineScope() // To run the background task
    val context = LocalContext.current


    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = { uri: Uri? ->
            uri?.let { selectedUri ->
                // Launch a background task
                scope.launch(Dispatchers.IO) {
                    try {
                        context.contentResolver.openInputStream(selectedUri)?.use { inputStream ->
                            // Example: Read bytes
                            val transactionList = readCsv(inputStream).reversed()
//                            val fileData = inputStream.readBytes()

                            print("here")
                            // Switch back to Main thread if you need to update UI state
                            withContext(Dispatchers.Main) {
                                // Update your database or UI here
                                for (transaction in transactionList) {
//                                    transaction.dateTime = Instant.now()
                                    db.transactionDao().insert(transaction)
                                }
                            }
                        }
                    } catch (e: Exception) {
                        // Handle errors (file not found, permission denied, etc)
                        e.printStackTrace()
                    }
                }
            }
        }
    )

    var showCreateTransactionForm by remember { mutableStateOf( false) }

    if (showCreateTransactionForm) {
        CreateTransactionForm (
            Modifier,
            db = db,
            { showCreateTransactionForm = false })
    }

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp)
        ) {
            items(
                items = transactionList,
                key = { it.uid }
            ) { transaction ->
                TransactionCard(
                    transaction = transaction,
                    categoryList = categoryList
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
                        launcher.launch(arrayOf("text/csv", "text/comma-separated-values"))
                    }
                ) {
                    Text(
                        text = "Upload"
                    )
                }
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
}

@Composable
fun TransactionCard(
    transaction: Transaction,
    categoryList: List<Category>
) {
    var mExpanded by rememberSaveable(transaction.uid) { mutableStateOf(false) }

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
                text = "Business",
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "${transaction.businessName}",
                modifier = Modifier.padding(bottom = 5.dp)
            )

            Text(
                text = "Date/Time: ${transaction.dateTime}",
                modifier = Modifier.padding(bottom = 5.dp)
            )

            if (transaction.debit != 0.0) {
                Text(
                    text = "+${transaction.debit}",
                    color = Color.Green,
                    modifier = Modifier.padding(bottom = 5.dp),
                    fontSize = 22.sp
                )
            } else if (transaction.credit != 0.0) {
                Text(
                    text = "-${transaction.credit}",
                    color = Color.Red,
                    modifier = Modifier.padding(bottom = 5.dp),
                    fontSize = 22.sp
                )
            }

            Box(modifier = Modifier.wrapContentSize(Alignment.TopStart)) {
                Button(
                    onClick = { mExpanded = true }
                ) {
                    Text(
                        text = "Category: ${if (transaction.categoryName == "") "None" else transaction.categoryName}",
                        color = if (transaction.categoryName == "") Color.Red else Color.Green
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
                                // Note: You'll likely want to pass a callback here to update the DB
                                mExpanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}


fun readCsv(inputStream: InputStream): List<Transaction> =
    CSVFormat.Builder.create(CSVFormat.DEFAULT).apply {
        setIgnoreSurroundingSpaces(true)
    }.build().parse(inputStream.reader())
        .drop(1) // Dropping the header
        .map {
            val businessName = it[2]
            val dateTime = it[1].toString().toInstant()
            val credit = it[3].toString().drop(1).toDouble()
//            val creditSlice = credit.slice(1..it[3].length).toDouble()
            print("here 2")

            Transaction(
                uid = getTransactionSha256Uid(businessName, credit, dateTime),
                categoryUid = -1,
                dateTime = dateTime,
                credit = credit,
                businessName = businessName
            )
        }

fun String.toInstant(): java.time.Instant {
    // 1. Define the format
    val formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy")

    // 2. Parse string to LocalDate
    val localDate = LocalDate.parse(this, formatter)

    // 3. Convert to Instant (at start of day in UTC)
    return localDate.atStartOfDay().toInstant(ZoneOffset.UTC)
}

// Request code for selecting a PDF document.
//const val PICK_PDF_FILE = 2
//
//fun openFile(pickerInitialUri: Uri = Uri.EMPTY) {
//    val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
//        addCategory(Intent.CATEGORY_OPENABLE)
//        type = "application/pdf"
//
//        // Optionally, specify a URI for the file that should appear in the
//        // system file picker when it loads.
//        putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri)
//    }
//
//    startActivityForResult(Activity(), intent, PICK_PDF_FILE, null)
//}
