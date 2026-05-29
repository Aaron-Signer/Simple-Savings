package com.example.simplesavings.composable

import android.annotation.SuppressLint
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.geometry.Size


import com.example.simplesavings.config.database.AppDatabase
import com.example.simplesavings.model.category.Category
import com.example.simplesavings.model.group.Group
import kotlinx.coroutines.launch

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.toSize
import com.example.simplesavings.model.category.SpendingType


@SuppressLint("UnrememberedMutableState")
@Composable
fun CreateCategoryForm(
    modifier: Modifier = Modifier,
    db: AppDatabase,
    onDismiss: () -> Unit,
    groupList: List<Group>,
    currentMonthString: String,
    currentYearString: String
) {
    val scope = rememberCoroutineScope()
    var categoryName by remember {mutableStateOf("")}
    var categoryPlanned by remember {mutableStateOf("")}
    var categorySpendingType by remember {mutableStateOf(SpendingType.FIXED)}

    var mExpanded by remember { mutableStateOf(false) }

    // Create a string value to store the selected city
    var selectedGroup by remember { mutableStateOf(Group(-1, "")) }

    var mTextFieldSize by remember { mutableStateOf(Size.Zero)}

    // Up Icon when expanded and down icon when collapsed
    val icon = if (mExpanded)
        Icons.Filled.KeyboardArrowUp
    else
        Icons.Filled.KeyboardArrowDown

    val groupList by db.groupDao().getAll(currentMonthString, currentYearString).collectAsState(initial = emptyList())
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
            modifier = Modifier.border(1.dp, Color.White)
        ) {
            Text(
                text = "Category Form",
                Modifier.padding(10.dp)
            )
            TextField(
                value = categoryName,
                onValueChange = { categoryName = it },
                label = { Text("Category Name")},
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxWidth(.75F)
            )

            TextField(
                value = categoryPlanned,
                onValueChange = { categoryPlanned = it },
                label = { Text("Planned Amount")},
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxWidth(.75F),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 10.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = categorySpendingType == SpendingType.FIXED,
                        onClick = { categorySpendingType = SpendingType.FIXED }
                    )
                    Text("Fixed")
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = categorySpendingType == SpendingType.VARIABLE,
                        onClick = { categorySpendingType = SpendingType.VARIABLE }
                    )
                    Text("Variable")
                }
            }

            Text(
                text = "Group: ${selectedGroup.name}",
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
                            text = "Select Group"
                        )
                    }
                    DropdownMenu(
                        expanded = mExpanded,
                        onDismissRequest = { mExpanded = false },
                    ) {
                        groupList.forEach { label ->
                            DropdownMenuItem(
                                text = { Text(text = label.name) },
                                onClick = {
                                    selectedGroup = label
                                    mExpanded = false
                                }
                            )
                        }
                    }
                }
            }
            Row (
                modifier = Modifier
                    .fillMaxWidth(.75F),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Button(
                    onClick = {
                        scope.launch {
                            db.categoryDao().insert(
                                Category(
                                    0,
                                    selectedGroup.uid,
                                    categoryName,
                                    planned = categoryPlanned.toDouble(),
                                    spendingType = categorySpendingType
                                )
                            )
                        }
                    },
                    enabled = categoryName != "" && selectedGroup.uid != -1
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
}
