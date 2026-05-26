package com.example.simplesavings.composable.budget

import android.annotation.SuppressLint
import android.graphics.Paint.Align
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.simplesavings.composable.CreateCategoryForm
import com.example.simplesavings.config.database.AppDatabase
import com.example.simplesavings.model.group.Group
import kotlinx.coroutines.launch

@Composable
fun BudgetScreen (
    modifier: Modifier = Modifier,
    db: AppDatabase
) {

    val typeColumnWidth = .15F
    val remainingColumnWidth = .25F
    val groupList by db.groupDao().getAll().collectAsState(initial = emptyList())

    for (group in groupList) {
        val categoryListForGroup by db.categoryDao().getCategoriesForGroup(group.uid).collectAsState(initial = emptyList())

        var groupPlannedTotal = 0.0
        var groupSpentTotal = 0.0

        for (category in categoryListForGroup) {
            val transactionListForCategory by db.transactionDao().getTransactionsForCategory(category.uid).collectAsState(initial = emptyList())

            for (transaction in transactionListForCategory) {
                category.spent -= transaction.debit
                category.spent += transaction.credit
            }

            groupPlannedTotal += category.planned
            groupSpentTotal += category.spent
        }

        group.plannedTotal = groupPlannedTotal
        group.spentTotal = groupSpentTotal

//        if (group.uid == 1 && group.spentTotal > 12.2)
//            print("Larger")
    }

    var showCard by remember { mutableStateOf( false) }
    var showCreateCategoryForm by remember { mutableStateOf( false) }
    val scope = rememberCoroutineScope()

    if (showCard) {
        CreateGroupCard(
            Modifier,
            db = db,
            {showCard = false})
    }

    if (showCreateCategoryForm) {
        CreateCategoryForm(
            Modifier,
            db = db,
            {showCreateCategoryForm = false},
            groupList)
    }

    Column(modifier = modifier
        .padding(10.dp, 15.dp)
        .verticalScroll(rememberScrollState()) ) {
        for (group in groupList) {

            ElevatedCard (
                elevation = CardDefaults.cardElevation (
                    defaultElevation = 6.dp
                ),
                modifier = Modifier.fillMaxSize().padding(20.dp)
            ) {
                Row (
                    modifier = Modifier.fillMaxSize().padding(5.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column (
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text (
                            text = group.name
                        )
                    }

                    Row (
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button (
                            onClick = {
                                showCreateCategoryForm = true
                            }
                        ) {
                            Text (
                                text = "Add Cat"
                            )
                        }
                        Button (
                            onClick = {
                                scope.launch {
                                    db.groupDao().delete(group)
                                }
                            }
                        ) {
                            Text (
                                text = "Del"
                            )
                        }
                    }
                }
                Row(
                    Modifier.padding(5.dp).fillMaxWidth(),

                    ) {
                    Text (
                        text = "",
                        Modifier.weight(.2F)
                    )
                    Text (
                        text = "$${"%.2f".format(group.plannedTotal)}",
                        Modifier.weight(.2F).padding(end = 5.dp),
                        textAlign = TextAlign.End
                    )
                    Text (
                        text = "$${"%.2f".format(group.spentTotal)}",
                        Modifier.weight(.2F).padding(end = 5.dp),
                        textAlign = TextAlign.End
                    )
                    Text (
                        text = "",
                        Modifier.weight(remainingColumnWidth)
                    )
                    Text (
                        text = "",
                        Modifier.weight(typeColumnWidth)
                    )
                }
                HorizontalDivider(thickness = 1.dp)
                Row (
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text (
                        text = "Category",
                        modifier = Modifier.weight(.2F),
                        textAlign = TextAlign.Center
                    )
                    Text (
                        text = "Planned",
                        Modifier.weight(.2F),
                        textAlign = TextAlign.Center
                    )
                    Text (
                        text = "Spent",
                        Modifier.weight(.2F),
                        textAlign = TextAlign.Center
                    )
                    Text (
                        text = "Remaining",
                        Modifier.weight(remainingColumnWidth),
                        textAlign = TextAlign.Center
                    )
                    Text (
                        text = "Type",
                        Modifier.weight(typeColumnWidth),
                        textAlign = TextAlign.Center
                    )
                }
                HorizontalDivider(thickness = 2.dp, color = Color.Cyan)

                Column() {
                    val categoryList by db.categoryDao().getCategoriesForGroup(group.uid).collectAsState(initial = emptyList())

                    for (category in categoryList) {
                        Row(
                            Modifier.padding(5.dp).fillMaxWidth(),

                            ) {
                            Text (
                                text = category.name,
                                Modifier.weight(.2F),
                            )
                            Text (
                                text = "$${"%.2f".format(category.planned)}",
                                Modifier.weight(.2F).padding(end = 5.dp),
                                textAlign = TextAlign.End
                            )
                            val percentageSpent = category.spent / category.planned
                            Text (
                                text = "$${"%.2f".format(category.spent)}",
                                Modifier.weight(.2F).padding(end = 5.dp),
                                textAlign = TextAlign.End,
                                color = if (percentageSpent in 0.0.. .5)
                                    Color.Green
                                else if (percentageSpent > .5 && percentageSpent < 1)
                                    Color.Yellow
                                else
                                    Color.Red
                            )
                            Text (
                                text = "$${"%.2f".format(category.planned - category.spent)}",
                                Modifier.weight(remainingColumnWidth).padding(end = 5.dp),
                                textAlign = TextAlign.End,
                                color = if (percentageSpent in 0.0.. .5)
                                    Color.Green
                                else if (percentageSpent > .5 && percentageSpent < 1)
                                    Color.Yellow
                                else
                                    Color.Red
                            )
                            Text (
                                text = category.spendingType.toString(),
                                Modifier.weight(typeColumnWidth)
                            )
                        }
                        HorizontalDivider(thickness = 1.dp)

                    }
                }

            }
        }
        Box (
            modifier = modifier.fillMaxSize().zIndex(100F).padding(bottom = 50.dp),
            contentAlignment = Alignment.BottomEnd,
        ) {
            Button (
                onClick = {
                    showCard = true
                }
            ) {
                Text (
                    text = "Add Group"
                )
            }
        }
    }
}

@SuppressLint("UnrememberedMutableState")
@Composable
fun CreateGroupCard(
    modifier: Modifier = Modifier,
    db: AppDatabase,
    onDismiss: () -> Unit) {
    val scope = rememberCoroutineScope()
    var groupName: MutableState<String> = mutableStateOf("")

    Box(
        modifier = modifier.fillMaxSize().zIndex(100F),
        contentAlignment = Alignment.Center
    ) {
        ElevatedCard(
            elevation = CardDefaults.cardElevation(
                defaultElevation = 6.dp
            ),
            modifier = Modifier.height(200.dp).width(200.dp)
        ) {
            TextField(
                value = groupName.value,
                onValueChange = { groupName.value = it }
            )
            Button(
                onClick = {
                    scope.launch {
                        db.groupDao().insert(Group(0, groupName.value))
                    }
                },
                enabled = groupName.value != ""
            ) {
                Text(
                    text = "Add Group"
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

