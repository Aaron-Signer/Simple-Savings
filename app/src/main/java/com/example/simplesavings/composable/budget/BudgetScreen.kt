package com.example.simplesavings.composable.budget

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.simplesavings.composable.CreateCategoryForm
import com.example.simplesavings.config.database.AppDatabase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun BudgetScreen (
    modifier: Modifier = Modifier,
    db: AppDatabase,
    currentMonthAndYear: Instant,
    monthFormatter: DateTimeFormatter,
    yearFormatter: DateTimeFormatter
) {

    var currentYearString = currentMonthAndYear
        .atZone(ZoneId.systemDefault())
        .format(yearFormatter)

    var currentMonthString = currentMonthAndYear
        .atZone(ZoneId.systemDefault())
        .format(monthFormatter)

    val groupList by db.groupDao().getAll(currentMonthString, currentYearString).collectAsState(initial = emptyList())

    var showCard by remember { mutableStateOf( false) }
    var showCreateCategoryForm by remember { mutableStateOf( false) }
    val scope = rememberCoroutineScope()

    if (showCard) {
        CreateGroupCard(
            Modifier,
            db = db,
            {showCard = false},
            currentMonthString = currentMonthString,
            currentYearString = currentYearString)
    }

    if (showCreateCategoryForm) {
        CreateCategoryForm(
            Modifier,
            db = db,
            {showCreateCategoryForm = false},
            groupList,
            currentMonthString,
            currentYearString)
    }
    Column(modifier = Modifier
        .padding(10.dp, 15.dp)
        .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(10.dp)) {
        if (groupList.isEmpty()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Button(
                    onClick = {
                        scope.launch {
                            copyGroupsAndCategories(
                                db,
                                currentMonthAndYear,
                                monthFormatter,
                                yearFormatter
                            )
                        }
                    }
                ) {
                    Text(
                        text = "Copy Last Month's Budget"
                    )
                }
            }
        } else {

            SavingsSummaryCard(
                db,
                currentMonthString,
                currentYearString
            )

            for (group in groupList) {

                GroupCard(
                    modifier = Modifier,
                    group = group,
                    db = db,
                    showCreateCategoryForm = { showCreateCategoryForm = true}
                )
            }
        }
        Box(
            modifier = modifier
                .fillMaxSize()
                .zIndex(100F)
                .padding(bottom = 50.dp),
            contentAlignment = Alignment.BottomEnd,
        ) {
            Button(
                onClick = {
                    showCard = true
                }
            ) {
                Text(
                    text = "Add Group"
                )
            }
        }
    }

}

suspend fun copyGroupsAndCategories(
    db: AppDatabase,
    currentMonthAndYear: Instant,
    monthFormatter: DateTimeFormatter,
    yearFormatter: DateTimeFormatter
) {
    val previousMonthAndYear = currentMonthAndYear
        .atZone(ZoneId.systemDefault())
        .minusMonths(1)
        .toInstant()

    val prevMonthString = previousMonthAndYear
        .atZone(ZoneId.systemDefault())
        .format(monthFormatter)

    val prevYearString = previousMonthAndYear
        .atZone(ZoneId.systemDefault())
        .format(yearFormatter)

    val currentMonthString = currentMonthAndYear
        .atZone(ZoneId.systemDefault())
        .format(monthFormatter)

    val currentYearString = currentMonthAndYear
        .atZone(ZoneId.systemDefault())
        .format(yearFormatter)

    val previousMonthGroupList = db.groupDao().getAll(prevMonthString, prevYearString).first()

    for (group in previousMonthGroupList) {
        val categoryList = db.categoryDao().getCategoriesForGroup(group.uid).first()

        val newGroup = group.copy(
            uid = 0,
            month = currentMonthString,
            year = currentYearString,
            plannedTotal = 0.0,
            spentTotal = 0.0
        )
        val insertedGroupUid = db.groupDao().insert(newGroup)

        for (category in categoryList) {
            val newCategory = category.copy(
                uid = 0,
                groupUid = insertedGroupUid.toInt(),
                spent = 0.0
            )
            db.categoryDao().insert(newCategory)
        }
    }
}

