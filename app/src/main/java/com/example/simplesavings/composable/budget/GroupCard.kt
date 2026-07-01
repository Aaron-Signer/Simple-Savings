package com.example.simplesavings.composable.budget

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddCircleOutline
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.EditOff
import androidx.compose.material.icons.outlined.RemoveCircleOutline
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.simplesavings.config.database.AppDatabase
import com.example.simplesavings.model.category.Category
import com.example.simplesavings.model.category.SpendingType
import com.example.simplesavings.model.group.Group
import kotlinx.coroutines.launch

val categoryColumnWidth = .3F
val plannedColumnWidth = .2F
val spentColumnWidth = .2F
val remainingColumnWidth = .2F
val typeColumnWidth = .1F

@Composable
fun GroupCard(
    modifier: Modifier,
    group: Group,
    db: AppDatabase,
    showCreateCategoryForm: () -> Unit

) {
    val scope = rememberCoroutineScope()
    var isEditMode by remember { mutableStateOf(false) }

    ElevatedCard(
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
        modifier = modifier
            .fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(
            containerColor = Color(0xFF664B47), // Set your background color here
            contentColor = Color.White         // Optional: Set default text color
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(5.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = group.name
                )
            }

            Row(
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(
                    onClick = {
                        isEditMode = !isEditMode
                    }
                ) {
                    Icon(
                        if (!isEditMode)
                            Icons.Outlined.Edit
                            else
                                Icons.Outlined.EditOff,
                        contentDescription = "Enable Category Edit Mode",
                        tint = Color.White
                    )
                }
                IconButton(
                    onClick = {
                        showCreateCategoryForm()
                    }
                ) {
                    Icon(
                        Icons.Outlined.AddCircleOutline,
                        contentDescription = "Add Group",
                        tint = Color.Green
                    )
                }
                IconButton(
                    onClick = {
                        scope.launch {
                            db.categoryDao().deleteCategoryByGroupUid(group.uid)
                            db.groupDao().delete(group)
                        }
                    }
                ) {
                    Icon(
                        Icons.Outlined.RemoveCircleOutline,
                        contentDescription = "Remove Group",
                        tint = Color.Red
                    )
                }
            }
        }
        Row(
            Modifier
                .padding(5.dp)
                .fillMaxWidth(),

            ) {
            Text(
                text = "",
                Modifier.weight(categoryColumnWidth)
            )
            Text(
                text = "$${"%.2f".format(group.plannedTotal)}",
                Modifier
                    .weight(plannedColumnWidth)
                    .padding(end = 5.dp),
                textAlign = TextAlign.End
            )
            Text(
                text = "$${"%.2f".format(group.spentTotal)}",
                Modifier
                    .weight(spentColumnWidth)
                    .padding(end = 5.dp),
                textAlign = TextAlign.End
            )
            Text(
                text = "",
                Modifier.weight(remainingColumnWidth)
            )
            Text(
                text = "",
                Modifier.weight(typeColumnWidth)
            )
        }
        HorizontalDivider(thickness = 1.dp)
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Category",
                modifier = Modifier.weight(categoryColumnWidth),
                textAlign = TextAlign.Center
            )
            Text(
                text = "Planned",
                Modifier.weight(plannedColumnWidth),
                textAlign = TextAlign.Center
            )
            Text(
                text = "Spent",
                Modifier.weight(spentColumnWidth),
                textAlign = TextAlign.Center
            )
            Text(
                text = "Remng",
                Modifier.weight(remainingColumnWidth),
                textAlign = TextAlign.Center
            )
            Text(
                text = "Type",
                Modifier.weight(typeColumnWidth),
                textAlign = TextAlign.Center
            )
        }
        HorizontalDivider(thickness = 2.dp, color = Color.Cyan)

        Column() {
            val categoryList by db.categoryDao().getCategoriesForGroup(group.uid)
                .collectAsState(initial = emptyList())

            for (category in categoryList) {
                Row(
                    Modifier
                        .padding(5.dp)
                        .fillMaxWidth(),

                    ) {
                    Text(
                        text = category.name,
                        Modifier.weight(categoryColumnWidth),
                    )
                    Text(
                        text = "$${"%.2f".format(category.planned)}",
                        Modifier
                            .weight(plannedColumnWidth)
                            .padding(end = 5.dp),
                        textAlign = TextAlign.End
                    )
                    val percentageSpent = category.spent / category.planned
                    Text(
                        text = "$${"%.2f".format(category.spent)}",
                        Modifier
                            .weight(spentColumnWidth)
                            .padding(end = 5.dp),
                        textAlign = TextAlign.End,
                        color = getTextColor(category)
                        )
                    Text(
                        text = "$${"%.2f".format(category.planned - category.spent)}",
                        Modifier
                            .weight(remainingColumnWidth)
                            .padding(end = 5.dp),
                        textAlign = TextAlign.End,
                        color = getTextColor(category)
                    )
                    Text(
                        text = if (category.spendingType == SpendingType.FIXED)
                                "FXD"
                                else if (category.spendingType == SpendingType.RECURRING) "REC"
                                else "VAR",
                        Modifier.weight(typeColumnWidth),
                        textAlign = TextAlign.Center
                    )
                    if (isEditMode) {
                        IconButton(
                            onClick = {
                                scope.launch {
//                                    db.categoryDao().deleteCategoryByGroupUid(group.uid)
                                    db.categoryDao().delete(category)
                                }
                            }
                        ) {
                            Icon(
                                Icons.Outlined.Edit,
                                contentDescription = "Edit Category",
                                tint = Color.White
                            )
                        }
                    }
                }
                HorizontalDivider(thickness = 1.dp)

            }
        }

    }
}

fun getTextColor(category: Category): Color {
    val percentageSpent = category.spent / category.planned

    if (category.spendingType == SpendingType.FIXED) {
        if (percentageSpent in 0.0.. .5)
            return Color.Green
        else if (percentageSpent > .5 && percentageSpent < 1)
            return Color.Yellow
        else
            return Color.White
    } else {
        if (percentageSpent in 0.0.. .5)
            return Color.Green
        else if (percentageSpent > .5 && percentageSpent < 1)
            return Color.Yellow
        else
            return Color(0xFFFF3D3D)
    }
}